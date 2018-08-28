package seedu.taskmaster.logic.parser;

import static seedu.taskmaster.commons.core.Messages.MESSAGE_ILLEGAL_DATE_INPUT;
import static seedu.taskmaster.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.taskmaster.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.taskmaster.commons.exceptions.IllegalValueException;
import seedu.taskmaster.commons.util.StringUtil;
import seedu.taskmaster.logic.commands.AddCommand;
import seedu.taskmaster.logic.commands.AddFloatingCommand;
import seedu.taskmaster.logic.commands.AddNonFloatingCommand;
import seedu.taskmaster.logic.commands.BlockCommand;
import seedu.taskmaster.logic.commands.ChangeDirectoryCommand;
import seedu.taskmaster.logic.commands.ClearCommand;
import seedu.taskmaster.logic.commands.Command;
import seedu.taskmaster.logic.commands.CompleteCommand;
import seedu.taskmaster.logic.commands.DeleteCommand;
import seedu.taskmaster.logic.commands.EditCommand;
import seedu.taskmaster.logic.commands.ExitCommand;
import seedu.taskmaster.logic.commands.FindCommand;
import seedu.taskmaster.logic.commands.HelpCommand;
import seedu.taskmaster.logic.commands.IncorrectCommand;
import seedu.taskmaster.logic.commands.ListCommand;
import seedu.taskmaster.logic.commands.RedoCommand;
import seedu.taskmaster.logic.commands.SelectCommand;
import seedu.taskmaster.logic.commands.UndoCommand;
import seedu.taskmaster.logic.commands.ViewCommand;
import seedu.taskmaster.logic.util.DateFormatterUtil;
import seedu.taskmaster.model.task.RecurringType;
import seedu.taskmaster.model.task.Task;
import seedu.taskmaster.model.task.TaskDate;

/**
 * Parses user input.
 */
public class Parser {
    private static final int RECURRING_PERIOD_OFFSET = 1;

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern FIND_ARGS_WITHOUT_DATE_FORMAT = Pattern
            .compile("(?<keywords>[^/]+)" + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern FIND_ARGS_WITH_DATE_FORMAT = Pattern
            .compile("(?<keywords>[^/]+)" + "((?<startTime>(?: from [^/]+)(?<endTime>(?: to [^/]+)))|"
                    + "(?<deadline>(?: by [^/]+)))" + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern FIND_ARGS_WITHOUT_KEYWORD_FORMAT = Pattern
            .compile("((?<startTime>(?:from [^/]+)(?<endTime>(?: to [^/]+)))|" + "(?<deadline>(?:by [^/]+)))"
                    + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern FIND_ARGS_WITH_TAG_FORMAT = Pattern.compile("(?<tagArguments>(?:t/[^/]+)*)");

    private static final Pattern EDIT_ARGS_WITHOUT_DATE_FORMAT = Pattern
            .compile("(?<targetIndex>[\\d]+)" + "(?<name> [^/]+)" + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern EDIT_ARGS_WITH_DATE_FORMAT = Pattern.compile(
            "(?<targetIndex>[\\d]+)" + "(?<name> [^/]+)" + "((?<startTime>(?: from [^/]+)(?<endTime>(?: to [^/]+)))|"
                    + "(?<deadline>(?: by [^/]+)))" + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern EDIT_ARGS_WITHOUT_NAME_FORMAT = Pattern
            .compile("(?<targetIndex>[\\d]+)" + "((?<startTime>(?: from [^/]+)(?<endTime>(?: to [^/]+)))|"
                    + "(?<deadline>(?: by [^/]+)))" + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern EDIT_ARGS_WITH_TAG_FORMAT = Pattern
            .compile("(?<targetIndex>[\\d]+)" + "(?<tagArguments>(?: t/[^/]+)*)");

    private static final Pattern FLOATING_TASK_DATA_ARGS_FORMAT = // '/' forward
                                                                  // slashes are
                                                                  // reserved
                                                                  // for
                                                                  // delimiter
                                                                  // prefixes
            Pattern.compile("(?<name>[^/]+)" + "(?<tagArguments>(?: t/[^/]+)*)"); // variable
                                                                                  // number
                                                                                  // of
                                                                                  // tags

    private static final Pattern NON_FLOATING_TASK_DATA_ARGS_FORMAT = Pattern
            .compile("(?<name>[^/]+)" + "((?<startTime>(?: from [^/]+)(?<endTime>(?: to [^/]+)))|"
                    + "(?<deadline>(?: by [^/]+)))" + "(?<tagArguments>(?: t/[^ ]+)*)"); // variable
                                                                                         // number
                                                                                         // of
                                                                                         // tags

    private static final Pattern RECURRING_TASK_DATA_ARGS_FORMAT = Pattern
            .compile("(?<recurring>\\b(?i)daily|weekly|monthly|yearly|none(?i)\\b)");

    private static final Pattern RECURRING_TASK_PERIOD_DATA_ARGS_FORMAT = Pattern
            .compile("repeat (?<period>\\d+)");    
    
    private static final Pattern BLOCK_DATA_ARGS_FORMAT = // '/' forward slashes
                                                          // are reserved for
                                                          // delimiter prefixes
            Pattern.compile("(?<startTime>(?:from [^/]+)(?<endTime>(?: to [^/]+)))" + "(?<tagArguments>(?: t/[^/]+)*)"); // variable
                                                                                                                         // number
                                                                                                                         // of
                                                                                                                         // tags

    private static final int DEADLINE_INDEX = 0;

    private static final int START_TIME_INDEX = 0;

    private static final int END_TIME_INDEX = 1;

    private static final int ONLY_DEADLINE = 1;

    private static final int TIME_PERIOD = 2;

    // For find/edit matcher
    private static Date startTime;

    private static Date endTime;

    private static Date deadline;

    private static String taskName;

    private static Set<String> keywordSet;

    private static Set<String> tagSet;

    private static RecurringType recurringType;

    private static int targetIndex;

    public Parser() {}

    /**
     * Parses user input into command for execution.
     *
     * @param userInput
     *            full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case EditCommand.COMMAND_WORD:
            return prepareEdit(arguments);

        case BlockCommand.COMMAND_WORD:
            return prepareBlock(arguments);

        case SelectCommand.COMMAND_WORD:
            return prepareSelect(arguments);

        case ChangeDirectoryCommand.COMMAND_WORD:
            return new ChangeDirectoryCommand(arguments.trim());

        case CompleteCommand.COMMAND_WORD:
            return prepareComplete(arguments);

        case DeleteCommand.COMMAND_WORD:
            return prepareDelete(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return prepareFind(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        case ViewCommand.COMMAND_WORD:
            return prepareView(arguments);

        default:
            return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    //@@author A0135782Y
    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args) {
        final Matcher matcherNonFloating = NON_FLOATING_TASK_DATA_ARGS_FORMAT.matcher(args.trim());
        if (!matcherNonFloating.matches()) {
            return prepareAddFloating(args);
        }
        return prepareAddNonFloating(args);
    }

    /**
     * Parses arguments in the context of adding a floating task
     * 
     * @param args
     *            full command args string
     * @return the prepared add floating command
     */
    private Command prepareAddFloating(String args) {
        final Matcher matcher = FLOATING_TASK_DATA_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                                      AddFloatingCommand.MESSAGE_USAGE));
        }
        try {
            return new AddFloatingCommand(matcher.group("name"), 
                                          getTagsFromArgs(matcher.group("tagArguments")));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of adding a non floating task
     * 
     * @param args
     *            full command args string
     * @return the prepared add non floating command
     */
    private Command prepareAddNonFloating(String args) {
        final Matcher matcher = NON_FLOATING_TASK_DATA_ARGS_FORMAT.matcher(args.trim());
        matcher.matches();
        if (matcher.group("deadline") != null) {
            return prepareAddNonFloatingByDate(matcher);
        } else {
            return prepareAddNonFloatingFromDateToDate(matcher);
        }
    }

    private RecurringType prepareRecurringTask(String args) {
        final Matcher matcher = RECURRING_TASK_DATA_ARGS_FORMAT.matcher(args.trim());
        RecurringType recurringType = RecurringType.IGNORED;
        if (!matcher.find()) {
            return recurringType;
        } else {
            recurringType = DateParser.getInstance().
                    extractRecurringInfo(matcher.group("recurring"));
        }
        return recurringType;
    }

    private int extractRecurringTaskPeriod(String args) {
        final Matcher matcher = RECURRING_TASK_PERIOD_DATA_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.find()) {
            return Task.NO_RECURRING_PERIOD;
        }
        try {
            final int period = DateParser.getInstance().extractRecurringPeriod(matcher.group("period"));
            return period - RECURRING_PERIOD_OFFSET;
        } catch (NumberFormatException nfe) {
            return Task.NO_RECURRING_PERIOD;
        }
    }

    /**
     * Prepares arguments in the context of adding a non floating task by date
     * only
     * 
     * @param matcher
     *            Contains the information we need
     * @param recurringType
     * @return the prepared add non floating command
     * @throws IllegalValueException
     *             Signals for incorrect command
     */
    private Command prepareAddNonFloatingByDate(Matcher matcher) {
        String endInput = matcher.group("deadline");
        RecurringType recurringType = prepareRecurringTask(endInput);
        if (recurringType == RecurringType.IGNORED) {
            recurringType = RecurringType.NONE;
        }
        final int repeatCount = extractRecurringTaskPeriod(endInput);
        try {
            return new AddNonFloatingCommand(
                   matcher.group("name"), getTagsFromArgs(matcher.group("tagArguments")),
                   new TaskDate(TaskDate.DATE_NOT_PRESENT), 
                   new TaskDate(DateParser.getInstance().getDateFromString(endInput).getTime()),
                   recurringType, repeatCount);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Prepares arguments in the context of adding a non floating task from date
     * to date
     * 
     * @param matcher
     *            Contains the information we need
     * @param recurringType
     * @return the prepared add non floating command
     * @throws IllegalValueException
     *             Signals for incorrect command
     */
    private Command prepareAddNonFloatingFromDateToDate(Matcher matcher) {
        String startInput = matcher.group("startTime");
        String endInput = matcher.group("endTime");
        RecurringType recurringType = prepareRecurringTask(endInput);
        if (recurringType == RecurringType.IGNORED) {
            recurringType = RecurringType.NONE;
        }
        final int repeatCount = extractRecurringTaskPeriod(endInput);        
        try {
            List<Date> datesToAdd = DateParser.getInstance().getFromToDatesFromString(startInput);
            return new AddNonFloatingCommand(
                   matcher.group("name"), getTagsFromArgs(matcher.group("tagArguments")),
                   new TaskDate(datesToAdd.get(START_TIME_INDEX)),
                   new TaskDate(datesToAdd.get(END_TIME_INDEX)), 
                   recurringType, repeatCount);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    //@@author

    // @@author A0147967J
    private Command prepareBlock(String args) {
        Matcher matcher = BLOCK_DATA_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, BlockCommand.MESSAGE_USAGE));
        }
        try {

            String startInput = matcher.group("startTime");
            List<Date> datesToAdd = DateParser.getInstance().getFromToDatesFromString(startInput);
            return new BlockCommand(getTagsFromArgs(matcher.group("tagArguments")),
                    new TaskDate(datesToAdd.get(START_TIME_INDEX)),
                    new TaskDate(datesToAdd.get(END_TIME_INDEX)));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    // @@author

    /**
     * Extracts the new task's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }

    /**
     * Parses arguments in the context of the delete task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {

        Optional<Integer> index = parseIndex(args);
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(index.get());
    }

    // @@author A0147967J
    /**
     * Parses arguments in the context of the complete task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareComplete(String args) {

        Optional<Integer> index = parseIndex(args);
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteCommand.MESSAGE_USAGE));
        }

        return new CompleteCommand(index.get());
    }
    // @@author

    /**
     * Parses arguments in the context of the select task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned
     * integer is given as the index. Returns an {@code Optional.empty()}
     * otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if (!StringUtil.isUnsignedInteger(index)) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    // @@author A0147995H
    /**
     * Initialize all the global variables to its initial value
     */
    private void initializeFindElements() {
        keywordSet = new HashSet<String>();
        startTime = null;
        endTime = null;
        deadline = null;
        tagSet = new HashSet<String>();
    }

    /**
     * Returns correct resulting command if the command is in the format of Find
     * TASK_NAME from DATE to DATE | by DATE t/TAG
     * 
     * @param m
     * @return the corresponding find command
     */
    private Command matchFindDateMatcher(Matcher m) {
        String[] keywords = m.group("keywords").split("\\s+");
        keywordSet = new HashSet<>(Arrays.asList(keywords));
        try {
            ArrayList<Date> dateSet = extractDateInfo(m);
            if (dateSet.size() == ONLY_DEADLINE) {
                deadline = dateSet.get(DEADLINE_INDEX);
            } else if (dateSet.size() == TIME_PERIOD) {
                startTime = dateSet.get(START_TIME_INDEX);
                endTime = dateSet.get(END_TIME_INDEX);
            }
            tagSet = getTagsFromArgs(m.group("tagArguments"));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        return new FindCommand(keywordSet, startTime, endTime, deadline, tagSet);
    }

    /**
     * Returns correct resulting command if the command is in the format of Find
     * from DATE to DATE | by DATE t/TAG
     * 
     * @param m
     * @return the corresponding find command
     */
    private Command matchFindNoKeywordMatcher(Matcher m) {
        try {
            ArrayList<Date> dateSet = extractDateInfo(m);
            if (dateSet.size() == ONLY_DEADLINE) {
                deadline = dateSet.get(DEADLINE_INDEX);
            } else if (dateSet.size() == TIME_PERIOD) {
                startTime = dateSet.get(START_TIME_INDEX);
                endTime = dateSet.get(END_TIME_INDEX);
            }
            tagSet = getTagsFromArgs(m.group("tagArguments"));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        return new FindCommand(keywordSet, startTime, endTime, deadline, tagSet);
    }

    /**
     * Returns correct resulting command if the command is in the format of Find
     * t/TAG
     * 
     * @param m
     * @return the corresponding find command
     */
    private Command matchFindTagMatcher(Matcher m) {
        final Collection<String> tagStrings = Arrays
                .asList(m.group("tagArguments").replaceFirst("t/", "").split(" t/"));
        tagSet = new HashSet<>(tagStrings);
        return new FindCommand(keywordSet, startTime, endTime, deadline, tagSet);
    }

    /**
     * Returns correct resulting command if the command is in the format of Find
     * TASK_NAME t/TAG
     * 
     * @param m
     * @return the corresponding find command
     */
    private Command matchFindNoDateMatcher(Matcher m) {
        String[] keywords = m.group("keywords").split("\\s+");
        keywordSet = new HashSet<>(Arrays.asList(keywords));
        try {
            tagSet = getTagsFromArgs(m.group("tagArguments"));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        return new FindCommand(keywordSet, startTime, endTime, deadline, tagSet);
    }

    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        if (args == null || args.length() == 0)
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        final Matcher noDateMatcher = FIND_ARGS_WITHOUT_DATE_FORMAT.matcher(args.trim());
        final Matcher dateMatcher = FIND_ARGS_WITH_DATE_FORMAT.matcher(args.trim());
        final Matcher tagMatcher = FIND_ARGS_WITH_TAG_FORMAT.matcher(args.trim());
        final Matcher noKeywordMatcher = FIND_ARGS_WITHOUT_KEYWORD_FORMAT.matcher(args.trim());

        initializeFindElements();

        boolean dateMatcherMatches = dateMatcher.matches();
        boolean noDateMatcherMatches = noDateMatcher.matches();
        boolean tagMatcherMatches = tagMatcher.matches();
        boolean noKeywordMatcherMathces = noKeywordMatcher.matches();

        if (dateMatcherMatches) {
            return matchFindDateMatcher(dateMatcher);
        } else if (noKeywordMatcherMathces) {
            return matchFindNoKeywordMatcher(noKeywordMatcher);
        } else if (tagMatcherMatches) {
            return matchFindTagMatcher(tagMatcher);
        } else if (noDateMatcherMatches) {
            return matchFindNoDateMatcher(noDateMatcher);
        } else {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Initialize all the global variables to its initial value
     */
    private void initializeEditElements() {
        taskName = "";
        startTime = null;
        endTime = null;
        tagSet = new HashSet<String>();
        recurringType = RecurringType.NONE;
        targetIndex = -1;
    }

    /**
     * Returns correct resulting command if the command is in the format of Edit
     * TARGET_INDEX TASK_NAME from DATE to DATE | by DATE RECURRING_TYPE t/TAG
     * 
     * @param m
     * @return corresponding edit command
     */
    private Command matchEditDateMatcher(Matcher m) {
        targetIndex = Integer.parseInt(m.group("targetIndex"));
        taskName = m.group("name").replaceFirst("\\s", "");

        try {
            ArrayList<Date> dateSet = extractDateInfo(m);
            if (dateSet.size() == ONLY_DEADLINE) {
                endTime = dateSet.get(DEADLINE_INDEX);
                recurringType = prepareRecurringTask(m.group("deadline"));
            } else if (dateSet.size() == TIME_PERIOD) {
                startTime = dateSet.get(START_TIME_INDEX);
                endTime = dateSet.get(END_TIME_INDEX);
                recurringType = prepareRecurringTask(m.group("startTime"));
            }

            tagSet = getTagsFromArgs(m.group("tagArguments"));
            return new EditCommand(targetIndex, taskName, tagSet, startTime, endTime, recurringType);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Returns correct resulting command if the command is in the format of Edit
     * TARGET_INDEX from DATE to DATE | by DATE RECURRING_TYPE t/TAG
     * 
     * @param m
     * @return corresponding edit command
     */
    private Command matchEditNoNameMatcher(Matcher m) {
        targetIndex = Integer.parseInt(m.group("targetIndex"));

        try {
            ArrayList<Date> dateSet = extractDateInfo(m);
            if (dateSet.size() == ONLY_DEADLINE) {
                endTime = dateSet.get(DEADLINE_INDEX);
                recurringType = prepareRecurringTask(m.group("deadline"));
            } else if (dateSet.size() == TIME_PERIOD) {
                startTime = dateSet.get(START_TIME_INDEX);
                endTime = dateSet.get(END_TIME_INDEX);
                recurringType = prepareRecurringTask(m.group("startTime"));
            }

            tagSet = getTagsFromArgs(m.group("tagArguments"));
            return new EditCommand(targetIndex, taskName, tagSet, startTime, endTime, recurringType);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Returns correct resulting command if the command is in the format of Edit
     * TARGET_INDEX from DATE to DATE | by DATE RECURRING_TYPE t/TAG
     * 
     * @param m
     * @return corresponding edit command
     */
    private Command matchEditTagMatcher(Matcher m) {
        targetIndex = Integer.parseInt(m.group("targetIndex"));
        try {
            tagSet = getTagsFromArgs(m.group("tagArguments"));
            return new EditCommand(targetIndex, taskName, tagSet, startTime, endTime, recurringType);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Returns correct resulting command if the command is in the format of Edit
     * TARGET_INDEX t/TAG
     * 
     * @param m
     * @return corresponding edit command
     */
    private Command matchEditNoDateMatcher(Matcher m) {
        targetIndex = Integer.parseInt(m.group("targetIndex"));
        taskName = m.group("name").replaceFirst("\\s", "");
        // -------Parts for detecting recurring information-----------------
        String[] words = taskName.split(" ");
        String lastWord = words[words.length - 1];
        recurringType = prepareRecurringTask(lastWord);
        if (recurringType != RecurringType.IGNORED) {
            taskName = (words.length == 1) ? "" : taskName.substring(0, taskName.length() - lastWord.length());
        }
        // -----------------------------------------------------------------
        try {
            tagSet = getTagsFromArgs(m.group("tagArguments"));
            return new EditCommand(targetIndex, taskName, tagSet, startTime, endTime, recurringType);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Returns different edit command according to the regex.
     * 
     * @param args
     * @return corresponding edit command
     */
    private Command prepareEdit(String args) {
        if (args == null || args.length() == 0)
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        final Matcher noDateMatcher = EDIT_ARGS_WITHOUT_DATE_FORMAT.matcher(args.trim());
        final Matcher dateMatcher = EDIT_ARGS_WITH_DATE_FORMAT.matcher(args.trim());
        final Matcher tagMatcher = EDIT_ARGS_WITH_TAG_FORMAT.matcher(args.trim());
        final Matcher noNameMatcher = EDIT_ARGS_WITHOUT_NAME_FORMAT.matcher(args.trim());

        initializeEditElements();

        boolean dateMatcherMatches = dateMatcher.matches();
        boolean noDateMatcherMatches = noDateMatcher.matches();
        boolean tagMatcherMatches = tagMatcher.matches();
        boolean noNameMatcherMathces = noNameMatcher.matches();

        if (dateMatcherMatches) {
            return matchEditDateMatcher(dateMatcher);
        } else if (noNameMatcherMathces) {
            return matchEditNoNameMatcher(noNameMatcher);
        } else if (tagMatcherMatches) {
            return matchEditTagMatcher(tagMatcher);
        } else if (noDateMatcherMatches) {
            return matchEditNoDateMatcher(noDateMatcher);
        } else {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
    }
    // @@author

    // @@author A0147967J
    /**
     * Returns the view command with input date parsed
     * 
     * @param arguments
     *            passed by user
     * @return prepared view command
     */
    private Command prepareView(String arguments) {
        // TODO Auto-generated method stub
        Date date;
        try {
            date = DateParser.getInstance().getDateFromString(arguments);
            date = DateFormatterUtil.getStartOfDay(date);
        } catch (IllegalValueException e) {
            return new IncorrectCommand(e.getMessage());
        }
        return new ViewCommand(new TaskDate(date));
    }
    // @@author

    // @@author A0147995H
    public static ArrayList<Date> extractDateInfo(Matcher m) throws IllegalValueException{
        ArrayList<Date> resultSet = new ArrayList<Date>();
        try {
            List<Date> datesToAdd = DateParser.getInstance().getFromToDatesFromString(m.group("startTime"));
            resultSet.clear();
            resultSet.add(datesToAdd.get(START_TIME_INDEX));
            resultSet.add(datesToAdd.get(END_TIME_INDEX));
        } catch (Exception ise) {
            resultSet.clear();
            try {
                resultSet.add(DateParser.getInstance().getDateFromString(m.group("deadline").replace(" by ", "")));
            } catch (Exception e) {
                throw new IllegalValueException(MESSAGE_ILLEGAL_DATE_INPUT);
            }
        }
        return resultSet;
    }
    // @@author

}

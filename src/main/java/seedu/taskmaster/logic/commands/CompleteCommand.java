package seedu.taskmaster.logic.commands;

import seedu.taskmaster.commons.core.Messages;
import seedu.taskmaster.commons.core.UnmodifiableObservableList;
import seedu.taskmaster.model.task.TaskOccurrence;
import seedu.taskmaster.model.task.UniqueTaskList.TaskNotFoundException;

//@@author A0147967J
/**
 * Marks a task as done identified using it's last displayed index from the task
 * list.
 */
public class CompleteCommand extends Command {

    public static final String COMMAND_WORD = "done";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives the task identified by the index number used in the last task listing. The Task will be deleted after exiting the app.\n"
            + "Parameters: INDEX (must be a positive integer)\n" + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_COMPLETE_TASK_SUCCESS = "Completed Task: %1$s";

    public final int targetIndex;

    public CompleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<TaskOccurrence> lastShownList = model.getFilteredTaskComponentList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            urManager.popFromUndoQueue();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        TaskOccurrence taskToArchive = lastShownList.get(targetIndex - 1);

        try {
            model.archiveTask(taskToArchive);
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_COMPLETE_TASK_SUCCESS, taskToArchive));
    }
}

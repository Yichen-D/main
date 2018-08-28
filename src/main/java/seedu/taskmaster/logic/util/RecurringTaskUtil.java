package seedu.taskmaster.logic.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;

import seedu.taskmaster.model.task.ReadOnlyTask;
import seedu.taskmaster.model.task.RecurringType;
import seedu.taskmaster.model.task.Task;
import seedu.taskmaster.model.task.TaskDate;
import seedu.taskmaster.model.task.TaskOccurrence;
import seedu.taskmaster.model.task.UniqueTaskList;

//@@author A0135782Y
/**
 * General utility class for recurring task manager
 */
public class RecurringTaskUtil {
    private static final int INVALID_PERIOD = -1;
    private static final int INDEPTH_CHECK_REQUIRED = 0;
    private static final int APPEND_INCREMENT = 1;
    private static final double NUM_MONTHS_IN_YEAR = 12.0;
    private static final double NUM_WEEKS_IN_MONTH = 4.0;
    private static final double NUM_DAYS_IN_WEEK = 7.0;
    private static final int NUMBER_OF_DAYS_IN_A_WEEK = 7;
    /**
     * Appends recurring tasks into TaskMaster 
     * 
     * @param task The recurring task
     * @param startDate Starting date of the recurring task, can be null
     * @param endDate Ending date of the recurring task, cannot be null
     * @param repeatingTasks Mutable list that is used to keep track of all Task Occurences
     * @param recurringType The recurring type of the task to be appended
     */
    public static void appendRecurringTask(ReadOnlyTask task, Calendar startDate, Calendar endDate, 
            UniqueTaskList repeatingTasks, RecurringType recurringType) {
        Calendar calendar = Calendar.getInstance();
        TaskDate editedStartDate = new TaskDate();
        TaskDate editedEndDate = new TaskDate();
        if (startDate != null) {
            calendar.setTime(startDate.getTime());
            addToCalendar(calendar,recurringType);
            editedStartDate.setDateInLong(calendar.getTime().getTime());
            startDate.setTime(editedStartDate.getDate());
        } else {
            editedStartDate.setDateInLong(TaskDate.DATE_NOT_PRESENT);
        }

        calendar.setTime(endDate.getTime());
        addToCalendar(calendar,recurringType);
        editedEndDate.setDateInLong(calendar.getTime().getTime());
        endDate.setTime(editedEndDate.getDate());   
        
        TaskOccurrence toAppend = new TaskOccurrence((Task) task, editedStartDate, editedEndDate);
        task.appendRecurringDate(toAppend);
        repeatingTasks.appendTaskOccurrence(toAppend);
    }
    
    /**
     * Corrects the start and end date of the recurring task
     * 
     * @param task The recurring task
     * @param elapsedPeriod The difference between the date it is added and the current date
     * @param recurringType Recurring type of the task
     */
    public static void correctRecurringTask(ReadOnlyTask task, int elapsedPeriod, RecurringType recurringType) {
        Calendar calendar = Calendar.getInstance();
        TaskDate correctedStartDate = new TaskDate();
        TaskDate correctedEndDate = new TaskDate();
        TaskDate startDate = task.getLastAppendedComponent().getStartDate();
        TaskDate endDate = task.getLastAppendedComponent().getEndDate();

        if (task.getLastAppendedComponent().isSlot()) {
            calendar.setTime(startDate.getDate());
            correctCalendarByElapsed(calendar, elapsedPeriod, recurringType);
            correctedStartDate.setDateInLong(calendar.getTime().getTime());
        } else {
            correctedStartDate.setDateInLong(TaskDate.DATE_NOT_PRESENT);
        }
        calendar.setTime(endDate.getDate());
        correctCalendarByElapsed(calendar, elapsedPeriod, recurringType);
        correctedEndDate.setDateInLong(calendar.getTime().getTime());

        task.getLastAppendedComponent().setStartDate(correctedStartDate);
        task.getLastAppendedComponent().setEndDate(correctedEndDate);        
    }
    
    /**
     * Helper method to correct the calendar date to the current date using the elapsed period
     * 
     * @param calendar An instance of calendar
     * @param elapsedPeriod The period between days or weeks or months or years
     * @param recurringType Recurring type of the task
     */
    private static void correctCalendarByElapsed(Calendar calendar, int elapsedPeriod, 
            RecurringType recurringType) {
        switch (recurringType) {
            case DAILY:
                calendar.add(Calendar.DAY_OF_MONTH, elapsedPeriod);
                break;
            case WEEKLY:
                calendar.add(Calendar.DAY_OF_MONTH, elapsedPeriod * NUMBER_OF_DAYS_IN_A_WEEK);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, elapsedPeriod);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, elapsedPeriod);
                break;
            default:
                assert false : "RecurringType cannot be NONE";
                break;                
        }
    }
    
    /**
     * Returns the number of elapsed time based on the task's recurring type
     * Elapsed time takes into account if the task has a start date or not.
     * If there is no start date, the end date is used to calculate the elapsed period.
     * 
     * @param localDateCurrently Current date as LocalDate object.
     * @param startDateInLocalDate Start date as LocalDate object, can be null.
     * @param endDateInLocalDate End Date as localDate object, cannot be null.
     * @param recurringType Recurring type of the task.
     * @return The number of elapsed time.
     */
    public static int getElapsedPeriodToAppend(LocalDate localDateCurrently, LocalDate startDateInLocalDate,
            LocalDate endDateInLocalDate, RecurringType recurringType) {
        final int elapsed;
        elapsed = getElapsedPeriodByRecurringType(
                          localDateCurrently, startDateInLocalDate, endDateInLocalDate, recurringType);
        return elapsed;
    }
    
    /**
     * Returns the elapsed period between the current date and the start or end date.
     * If start date is not present, the end date is used to get the elapsed period.
     * 
     * @param localDateCurrently The current date as LocalDate object.
     * @param startDateInLocalDate The start date as LocalDate object, can be null.
     * @param endDateInLocalDate The end date as LocalDate object, cannot be null.
     * @param recurringType The recurring type of the task
     * @return The elapsed period based on the recurring type and based on start or end date.
     */
    public static int getElapsedPeriodToCorrect(LocalDate localDateCurrently, LocalDate startDateInLocalDate,
            LocalDate endDateInLocalDate, RecurringType recurringType) {
        final int elapsedPeriod;
        if (startDateInLocalDate != null) {
            elapsedPeriod = getPeriodBetweenDatesToCorrect(
                                    startDateInLocalDate, localDateCurrently, recurringType);
        } else {
            elapsedPeriod = getPeriodBetweenDatesToCorrect(
                                    endDateInLocalDate, localDateCurrently, recurringType);
        }
        return elapsedPeriod;
    }
    
    /**
     * Return the elapsed time between two dates.
     * Elapsed time calculated is based on the recurring type.
     * 
     * @return The elapsed time between two dates and based on the recurring type.
     */
    private static int getPeriodBetweenDatesToCorrect(LocalDate before, LocalDate after, 
            RecurringType recurringType) {
        int elapsedPeriod;
        switch (recurringType) {
            case DAILY:
                elapsedPeriod = (int) ChronoUnit.DAYS.between(before, after);
                break;
            case WEEKLY:
                elapsedPeriod = (int) Math.ceil(ChronoUnit.DAYS.between(before, after) / NUM_DAYS_IN_WEEK);
                break;
            case MONTHLY:
                elapsedPeriod = (int) Math.ceil(ChronoUnit.WEEKS.between(before, after) / NUM_WEEKS_IN_MONTH);
                if (elapsedPeriod == INDEPTH_CHECK_REQUIRED) {
                    elapsedPeriod = getPeriodBetweenDatesToCorrect(before,after,RecurringType.DAILY);
                }
                break;
            case YEARLY:
                elapsedPeriod = (int) Math.ceil(ChronoUnit.MONTHS.between(before, after) / NUM_MONTHS_IN_YEAR);
                if (elapsedPeriod == INDEPTH_CHECK_REQUIRED) {
                    elapsedPeriod = getPeriodBetweenDatesToCorrect(before,after,RecurringType.DAILY);
                }
                break;
            default:
                elapsedPeriod = INVALID_PERIOD;
                assert false : "RecurringType cannot be NONE";
                break;
        }
        return elapsedPeriod;
    }
    
    /**
     * Helper method to add to calendar based on the recurring type.
     * 
     * @param calendar An instance of Calendar.
     * @param recurringType Used to add determine what values are added to the calendar.
     */
    private static void addToCalendar(Calendar calendar, RecurringType recurringType) {
        switch (recurringType) {
            case DAILY:
                calendar.add(Calendar.DAY_OF_MONTH, APPEND_INCREMENT);
                break;
            case WEEKLY:
                calendar.add(Calendar.DAY_OF_MONTH, NUMBER_OF_DAYS_IN_A_WEEK);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, APPEND_INCREMENT);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, APPEND_INCREMENT);
                break;
            default:
                assert false : "RecurringType cannot be NONE";
                break;
        }
    }
    
    /**
     * Returns the number of elapsed unit based on the recurring type.
     * Elapsed unit is calculated using the current date and  start date or the end date.
     * If start date is null then end date is used to calculate elapsed.
     * 
     * @param localDateCurrently Current date as a LocalDate object.
     * @param startDateInLocalDate Start date as a LocalDate object, can be null.
     * @param endDateInLocalDate End Date as a LocalDate object, cannot be null.
     * @param recurringType Recurring type of the task.
     * @return The number of elapsed unit.
     */
    private static int getElapsedPeriodByRecurringType(LocalDate localDateCurrently,
            LocalDate startDateInLocalDate, LocalDate endDateInLocalDate, RecurringType recurringType) {
        final int elapsed;
        switch (recurringType) {
            case DAILY:
                if (startDateInLocalDate != null) {
                    elapsed = (int) ChronoUnit.DAYS.between(startDateInLocalDate, localDateCurrently);
                } else {
                    elapsed = (int) ChronoUnit.DAYS.between(endDateInLocalDate, localDateCurrently);
                }
                break;
            case WEEKLY:
                if (startDateInLocalDate != null) {
                    elapsed = (int) Math.ceil(ChronoUnit.DAYS.between(startDateInLocalDate, localDateCurrently) 
                                               / NUM_DAYS_IN_WEEK);
                } else {
                    elapsed = (int) Math.ceil(ChronoUnit.DAYS.between(endDateInLocalDate, localDateCurrently) 
                                               / NUM_DAYS_IN_WEEK);
                }
                break;
            case MONTHLY:
                if (startDateInLocalDate != null) {
                    elapsed = (int) Math.ceil(ChronoUnit.WEEKS.between(startDateInLocalDate, localDateCurrently) 
                                              / NUM_WEEKS_IN_MONTH);
                } else {
                    elapsed = (int) Math.ceil(ChronoUnit.WEEKS.between(endDateInLocalDate, localDateCurrently) 
                                              / NUM_WEEKS_IN_MONTH);
                }                
                break;
            case YEARLY:
                if (startDateInLocalDate != null) {
                    elapsed = (int) Math.ceil(ChronoUnit.MONTHS.between(startDateInLocalDate, localDateCurrently) 
                                              / NUM_MONTHS_IN_YEAR);
                } else {
                    elapsed = (int) Math.ceil(ChronoUnit.MONTHS.between(endDateInLocalDate, localDateCurrently) 
                                              / NUM_MONTHS_IN_YEAR);
                }                
                break;
            default:
                elapsed = INVALID_PERIOD;
                assert false : "Recurring Type must not be NONE";
        }
        return elapsed;
    }

    /**
     * Gets the start date as a Calendar object.
     * 
     * @param target Cannot be null
     * @return null if there is no start date in target
     */
    public static Calendar getStartCalendar(TaskOccurrence target) {
        if (!target.getStartDate().isPresent()) {
            return null;
        }
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(target.getStartDate().getDate());
        return startDate;
    }

    /**
     * Gets the end date as a Calendar object.
     * 
     * @param target Cannot be null
     * @return end date of target as Calendar object.
     */    
    public static Calendar getEndCalendar(TaskOccurrence target) {
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(target.getEndDate().getDate());
        return endDate;
    }

    /**
     * Gets the start date as a LocalDate object.
     * 
     * @param target Cannot be null
     * @return null if there is no start date in target
     */
    public static LocalDate getStartLocalDate(TaskOccurrence target) {
        if (!target.isSlot()) {
            return null;
        }
        return DateFormatterUtil.dateToLocalDate(target.getStartDate().getDate());
    }

    /**
     * Gets the end date as a LocalDate object.
     * 
     * @param target Cannot be null
     * @return end date of target as LocalDate object.
     */        
    public static LocalDate getEndLocalDate(TaskOccurrence target) {
        return DateFormatterUtil.dateToLocalDate(target.getEndDate().getDate());
    }
}

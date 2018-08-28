package seedu.taskmaster.model.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import seedu.taskmaster.commons.util.CollectionUtil;
import seedu.taskmaster.model.tag.UniqueTagList;

/**
 * Represents a Task in the task list. 
 * A Floating task is created by using the constructor with only the (Name and UniqueTagList) 
 * E.g. Task floating = new Task(name, uniqueTagList) 
 * A Non Floating task is created by using the constructor with (Name, UniqueTagList, TaskDate, TaskDate) 
 * E.g. TaskDate startDate, endDate; startDate = new TaskDate(...); 
 * endDate = new TaskDate(...); Task nonFloating = new Task(name, uniqueTagList, startDate, endDate); 
 * Guarantees: details are present and not null, field values are validated.
 */
public class Task implements ReadOnlyTask {
    private static final int PERIOD_DECREMENT = 1;
    private static final int INDEX_OFFSET = 1;
    public static final int NO_RECURRING_PERIOD = -1;
    
    private Name name;
    private UniqueTagList tags;
    private TaskType taskType;
    private RecurringType recurringType;
    private int recurringPeriod;
    private List<TaskOccurrence> recurringDates;

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(name, tags);
        this.name = name;
        this.tags = tags;
        this.taskType = TaskType.FLOATING;
        this.recurringType = RecurringType.NONE;
        this.recurringDates = new ArrayList<TaskOccurrence>();
        this.recurringDates.add(new TaskOccurrence(this, new TaskDate(), new TaskDate()));
        this.recurringPeriod = NO_RECURRING_PERIOD;
    }

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, UniqueTagList tags, TaskDate startDate, TaskDate endDate, RecurringType recurringType, int recurringPeriod) {
        this(name, tags);
        assert !CollectionUtil.isAnyNull(startDate, endDate, recurringType);
        this.taskType = TaskType.NON_FLOATING;
        this.recurringType = recurringType;
        getLastAppendedComponent().setStartDate(startDate);
        getLastAppendedComponent().setEndDate(endDate);
        this.recurringPeriod = recurringPeriod;
    }

    private Task(Name name, UniqueTagList tags, RecurringType recurringType, int recurringPeriod) {
        this(name, tags);
        assert recurringType != null : "Recurring Type must be specified";
        this.recurringType = recurringType;
        this.recurringPeriod = recurringPeriod;
    }

    public Task() {
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getTags(), 
             source.getRecurringType(), source.getRecurringPeriod());
        this.recurringDates = source.getTaskDateComponent();
        this.taskType = source.getTaskType();
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }

    // @@author A0135782Y
    @Override
    public List<TaskOccurrence> getTaskDateComponent() {
        return recurringDates;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public RecurringType getRecurringType() {
        return recurringType;
    }

    public void setTaskType(TaskType type) {
        this.taskType = type;
    }

    /**
     * Sets the recurring type for the Task.
     * Floating Task cannot have a recurring type.
     * 
     * @param type The recurring type, cannot be null
     */
    public void setRecurringType(RecurringType type) {
        if (taskType == TaskType.FLOATING) {
            assert (type.equals(RecurringType.NONE)) : "Floating Task cannot be a recurring task";
        }
        this.recurringType = type;
    }

    // @@author
    // @@author A0147967J
    public void setRecurringDates(List<TaskOccurrence> newComponentList) {
        this.recurringDates = newComponentList;
    }

    // @@author
    /**
     * Replaces this task's tags with the tags in the argument tag list.
     */
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                        && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing
        // your own
        return Objects.hash(name, tags);
    }

    @Override
    public String toString() {
        return getAsText();
    }

    // @@author A0135782Y
    /**
     * Mark a task completed if all of its TaskOccurrences are archived.
     */
    @Override
    public void completeTaskWhenAllOccurrencesArchived() {
        for (TaskOccurrence c : recurringDates) {
            if (c.isArchived() == false 
                    || c.getTaskReference().getRecurringType() != RecurringType.NONE) {
                return;
            }
        }
        taskType = TaskType.COMPLETED;
    }
    // @@author

    // @@author A0147995H
    @Override
    public void updateTask(Name name, UniqueTagList tags, TaskDate startDate, TaskDate endDate,
            RecurringType recurringType, int index) {
        if (name != null)
            this.name = name;

        if (tags != null)
            this.tags = tags;

        if (this.getLastAppendedComponent().getStartDate().equals(new TaskDate(TaskDate.DATE_NOT_PRESENT))
                && this.getLastAppendedComponent().getStartDate().equals(new TaskDate(TaskDate.DATE_NOT_PRESENT))
                && endDate != null) {
            this.taskType = TaskType.NON_FLOATING;
        }

        if (endDate != null)
            this.recurringDates.get(index).update(startDate, endDate);

        if (recurringType != RecurringType.IGNORED)
            this.recurringType = recurringType;

        this.recurringDates.get(index).setTaskReferrence(this);
    }
    // @@author

    // @@author A0135782Y

    @Override
    public TaskOccurrence getLastAppendedComponent() {
        return recurringDates.get(recurringDates.size() - INDEX_OFFSET);
    }

    /**
     * Appends a recurring task with a task occurrence
     * Non Recurring Task cannot be appended with task occurrence.
     */
    @Override
    public void appendRecurringDate(TaskOccurrence componentToBeAdded) {
        assert !recurringType.equals(RecurringType.NONE) : "You cannot append new dates to non recurring tasks";
        recurringDates.add(componentToBeAdded);
        recurringDates.get(recurringDates.size() - INDEX_OFFSET).setTaskReferrence(this);
    }
    
    /**
     * Returns the recurring period of the task
     * Non Recurring Task will not have a valid recurring period.
     */
    @Override
    public int getRecurringPeriod() {
        if (recurringType.equals(RecurringType.NONE)) {
            return NO_RECURRING_PERIOD;
        }
        return recurringPeriod;
    }
    
    public int decrementRecurringPeriod() {
        recurringPeriod -= PERIOD_DECREMENT;
        return recurringPeriod;
    }
    // @@author
}

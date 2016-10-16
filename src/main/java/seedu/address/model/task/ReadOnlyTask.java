package seedu.address.model.task;

import java.util.List;

import seedu.address.model.tag.UniqueTagList;

/**
 * A read-only immutable interface for a Task in the task list.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyTask {

    Name getName();

    /**
     * The returned TagList is a deep copy of the internal TagList,
     * changes on the returned list will not affect the task's internal tags.
     */
    UniqueTagList getTags();

    TaskDate getStartDate();
    TaskDate getEndDate();
    List<TaskDateComponent> getTaskDateComponent();
    
    /**
     * Returns the type of the class, whether it is FLOATING or NON_FLOATING type
     */
    TaskType getTaskType();
    
    RecurringType getRecurringType();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyTask other) {
    		return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getTaskType().equals(this.getTaskType())
                && other.getStartDate().equals(this.getStartDate())
                && other.getEndDate().equals(this.getEndDate())
                );
    }

    /**
     * Formats the task as text, showing all contact details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        if(getStartDate().getDateInLong()!=-1){
        	builder.append(" From: ");
        	builder.append(getStartDate().getFormattedDate());
        }
        if(getEndDate().getDateInLong()!=-1){
        	builder.append(" To: ");
        	builder.append(getEndDate().getFormattedDate());
        }
        builder.append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Returns a string representation of this Task's tags
     */
    default String tagsString() {
        final StringBuffer buffer = new StringBuffer();
        final String separator = ", ";
        getTags().forEach(tag -> buffer.append(tag).append(separator));
        if (buffer.length() == 0) {
            return "";
        } else {
            return buffer.substring(0, buffer.length() - separator.length());
        }
    }

    void completeTaskWhenAllComponentArchived();
}

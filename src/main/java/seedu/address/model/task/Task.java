package seedu.address.model.task;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.model.tag.UniqueTagList;

import java.util.Objects;

/**
 * Represents a Task in the task list.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Task implements ReadOnlyTask {

    private Name name;
    private UniqueTagList tags;
    
    private TaskDate startDate, endDate;
    private TaskType type;
    
    /**
     * Every field must be present and not null.
     */
    public Task(Name name, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(name, tags);
        this.name = name;
        this.tags = tags;
        this.startDate = new TaskDate(TaskDate.DATE_NOT_PRESENT);
        this.endDate = new TaskDate(TaskDate.DATE_NOT_PRESENT);
        type = TaskType.FLOATING;
    }
    
    public Task(Name name, UniqueTagList tags, TaskDate startDate, TaskDate endDate) {
        this(name, tags);
        assert !CollectionUtil.isAnyNull(name, tags);
        this.startDate = startDate;
        this.endDate = endDate;
        type = TaskType.NON_FLOATING;
    }
    
    public Task(){}
    
    public boolean isValidTimeSlot(){
    	if(startDate!=null && endDate!=null){
    		return (endDate.getParsedDate()).after(startDate.getParsedDate());
    	}else{
    		return true;
    	}
    	
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getTags(), source.getStartDate(), source.getEndDate());
        if (source.getEndDate().getDate() == TaskDate.DATE_NOT_PRESENT) {
            type = TaskType.FLOATING;
        }
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }
    
    @Override
    public TaskDate getStartDate() {
        return startDate;
    }

    @Override
    public TaskDate getEndDate() {
        return endDate;
    }

    public TaskType getType() {
        return type;
    }
    
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
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, tags);
    }

    @Override
    public String toString() {
        return getAsText();
    }

}
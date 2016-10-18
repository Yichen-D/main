package seedu.address.model.task;

import seedu.address.commons.util.CollectionUtil;

/**
 * This class served as the occurrence portion in an abstraction occurrence pattern.
 * The abstraction is the Task and the occurrence is the TaskDateComponent.
 * @author User
 *
 */
public class TaskDateComponent {
    private Task taskReference;
    private TaskDate startDate, endDate;
    private boolean isArchived;
    
    public TaskDateComponent(Task taskReference) {
        assert taskReference != null : "Reference must not be null";
        this.taskReference = taskReference; 
        this.startDate = new TaskDate(TaskDate.DATE_NOT_PRESENT);
        this.endDate = new TaskDate(TaskDate.DATE_NOT_PRESENT);
        isArchived = false;
    }
    
    public TaskDateComponent(Task taskReference,TaskDate startDate, TaskDate endDate) {
        assert !CollectionUtil.isAnyNull(startDate, endDate);
        assert taskReference != null : "Reference must not be null";
        this.taskReference = taskReference;
        this.startDate = new TaskDate(startDate);
        this.endDate = new TaskDate(endDate);
    }
    
    /** copy constructor */
    public TaskDateComponent(TaskDateComponent taskDateComponent) {
        this.taskReference = taskDateComponent.taskReference;
        this.startDate = taskDateComponent.startDate;
        this.endDate = taskDateComponent.endDate;
        this.isArchived = taskDateComponent.isArchived;
    }

    public void setStartDate(TaskDate startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(TaskDate endDate) {
        this.endDate = endDate;
    }
    
    public TaskDate getStartDate() {
        return startDate;
    }
    
    public TaskDate getEndDate() {
        return endDate;
    }
    
    /**
     * Checks if TaskDateComponent is in a valid time slot
     * 
     * @return True if it is in a valid time slot
     */
    public boolean isValidTimeSlot(){
        if(startDate!=null && endDate!=null){
            return (endDate.getDate()).after(startDate.getDate());
        }else{
            return true;
        }
    }
    
    /**
     * Checks if the TaskDate has only end date
     * 
     * @return True if it has only an end date.
     *          False if it also contains a start date.
     */
    public boolean hasOnlyEndDate() {
        if (startDate.getDateInLong() != TaskDate.DATE_NOT_PRESENT){
            return false;
        }
        return true;
    }
    

    public ReadOnlyTask getTaskReference() {
        return taskReference;
    }

    /**
     * Archive this task component
     */
    public void archive() {
        isArchived = true;
    }
    
    public boolean isArchived() {
        return isArchived;
    }
    
    private boolean isSameStateAs(TaskDateComponent other) {
        return other == this // short circuit if same object
            || (other != null // this is first to avoid NPE below
            && other.getTaskReference().getName().equals(this.getTaskReference().getName()) // state checks here onwards
            && other.getTaskReference().getTaskType().equals(this.getTaskReference().getTaskType())
            && other.getStartDate().equals(this.getStartDate())
            && other.getEndDate().equals(this.getEndDate())
            );
    }
    
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskDateComponent // instanceof handles nulls
                && this.isSameStateAs((TaskDateComponent) other));        
    }
    
}

package seedu.taskmaster.testutil;

import java.util.ArrayList;
import java.util.List;

import com.joestelmach.natty.Parser;

import seedu.taskmaster.model.tag.UniqueTagList;
import seedu.taskmaster.model.task.*;

/**
 * A mutable task object. For testing only.
 */
public class TestTask extends Task implements ReadOnlyTask {

    private Name name;
    private UniqueTagList tags;
    private TaskType type = TaskType.FLOATING;

    private RecurringType recurringType;
    private int recurringPeriod;
    private List<TaskOccurrence> recurringDates;

    public TestTask() {
        tags = new UniqueTagList();
        recurringType = RecurringType.NONE;
        recurringDates = new ArrayList<TaskOccurrence>();
        recurringDates.add(new TaskOccurrence(this ,new TaskDate(), new TaskDate()));
        recurringPeriod = Task.NO_RECURRING_PERIOD;
    }

    public void setName(Name name) {
        this.name = name;
    }
    
    public void setTaskType(TaskType type){
    	this.type = type;
    }
    
    public void setTag(UniqueTagList tags){
    	this.tags = tags;
    }
    
    //Currently, only for non recurring tasks
    public void setStartDate(String date){
    	com.joestelmach.natty.Parser p = new Parser();
    	TaskDate startDate = new TaskDate(p.parse(date).get(0).getDates().get(0).getTime());
    	for(TaskOccurrence taskComponent: recurringDates)
    		taskComponent.setStartDate(startDate);
    	setTaskType(TaskType.NON_FLOATING);
    }
    
    public void setEndDate(String date){
    	com.joestelmach.natty.Parser p = new Parser();
    	TaskDate endDate = new TaskDate(p.parse(date).get(0).getDates().get(0).getTime());
    	for(TaskOccurrence taskComponent: recurringDates)
    		taskComponent.setEndDate(endDate);
    	setTaskType(TaskType.NON_FLOATING);
    }
    
    public void setRecurringType(RecurringType type) {
        this.recurringType = type;
    }
    
    public void setRecurringPeriod(int period) {
        this.recurringPeriod = period;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public UniqueTagList getTags() {
        return tags;
    }
    
    @Override
    public RecurringType getRecurringType() {
        return recurringType;
    }
    
    @Override
    public int getRecurringPeriod() {
        return recurringPeriod;
    }    
    
    @Override
    public List<TaskOccurrence> getTaskDateComponent() {
        return recurringDates;
    }
    
    @Override
    public TaskOccurrence getLastAppendedComponent() {
        return recurringDates.get(recurringDates.size()-1);
    }

    @Override
    public String toString() {
        return getAsText();
    }
    
    @Override
    public TaskType getTaskType(){
    	return type;
    }
    
    @Override
    public void appendRecurringDate(TaskOccurrence componentToBeAppended) {
        assert !recurringType.equals(RecurringType.NONE) : "You cannot append new dates to non recurring tasks";
        recurringDates.add(componentToBeAppended);        
    }
    //@@author A0147967J
    public String getAddFloatingCommand() {
    	this.type = TaskType.FLOATING;
        return getAddCommand();
    }
    
    public String getAddNonFloatingCommand() {
    	this.type = TaskType.NON_FLOATING;
    	return getAddCommand();
    }
    
    public String getAddRecurringCommand(){
    	this.type = TaskType.NON_FLOATING;
        return getAddCommand();
    }
    
    public String getBlockCommand() {
    	this.type = TaskType.NON_FLOATING;
        StringBuilder sb = new StringBuilder();
        sb.append("block ");
        sb.append("from "+ this.getLastAppendedComponent().getStartDate().getInputDate() + " ");
        sb.append("to "+ this.getLastAppendedComponent().getEndDate().getInputDate() + " ");
        this.getTags().getInternalList().stream().forEach(s -> sb.append("t/" + s.tagName + " "));
        return sb.toString();
    }
    
    public boolean equals(TestTask toCompare) {
        return this.isSameStateAs(toCompare);
    }
    
    private String getAddCommand(){
    	StringBuilder sb = new StringBuilder();
        sb.append("add " + this.getName().fullName + " ");
        if(this.type != TaskType.FLOATING){
        	if(this.getLastAppendedComponent().isDeadline()){
            	sb.append("by "+ this.getLastAppendedComponent().getEndDate().getInputDate() + " ");
            }else{
            	sb.append("from "+ this.getLastAppendedComponent().getStartDate().getInputDate() + " ");
            	sb.append("to "+ this.getLastAppendedComponent().getEndDate().getInputDate() + " ");
            }
        	if(this.recurringType!=RecurringType.NONE)
        		sb.append(this.getRecurringType().toString() + " ");
        }
        this.getTags().getInternalList().stream().forEach(s -> sb.append("t/" + s.tagName + " "));
        return sb.toString();       
    }
}

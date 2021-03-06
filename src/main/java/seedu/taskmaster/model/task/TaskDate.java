package seedu.taskmaster.model.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import seedu.taskmaster.logic.util.DateFormatterUtil;

//@@author A0135782Y
/**
 * Helper class for storing date for the Task
 */
public class TaskDate {
    private static final String NO_DATE_PRESENT = "";
    public static final int DATE_NOT_PRESENT = -1;
    private long date;
    
    /**
     * Date is not present by default if nothing is specified
     * Convenience and defensive
     */
    public TaskDate() {
        this.date = DATE_NOT_PRESENT;
    }
    
    public TaskDate(Date date) {
        this.date = date.getTime();
    }
    
    public TaskDate(long date) {
        this.date = date;
    }
        
    public TaskDate(TaskDate copy) {
        this.date = copy.date;
    }
    
    //@@author
    //For sake of testing, not implemented in main app
    public TaskDate(String inputDate) {
        this.date = new com.joestelmach.natty.Parser().parse(inputDate).get(0).getDates().get(0).getTime();
    }
    
    //@@author A0135782Y
    public void setDateInLong(long date) {
        this.date = date;
    }
    /**
     * Formats the date in (EEE, MMM d hh.mma) format which will give MON, Oct 20 10.00PM
     * If there is no date present return empty string
     * @return Empty string if there is no date present
     *          Formatted date if there is date
     */
    public String getFormattedDate() {
        if (date == DATE_NOT_PRESENT) {
            return NO_DATE_PRESENT;
        }
        return DateFormatterUtil.getFormattedDate(new Date(date));
    }
    //@author
    
    //For sake of testing
    public String getInputDate() {
        if (date == DATE_NOT_PRESENT) {
            return NO_DATE_PRESENT;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM hha", Locale.ENGLISH);
        return formatter.format(new Date(date));
    }
    
    //@@author A0135782Y
    public long getDateInLong() {
        return date;
    } 
    
    /**
     * Parses the date in Long and provides it in the Date class format
     */
    public Date getDate() {
    	return new Date(date);
    }
    
    @Override
    public boolean equals(Object other){
		return other == this 
		       || (other instanceof TaskDate // instance of handles nulls
		           && this.getDate().equals(((TaskDate) other).getDate()));
    }

    public boolean isPresent() {
        return date != DATE_NOT_PRESENT;
    }
    
    @Override
    public String toString() {
        return getFormattedDate();
    }
}

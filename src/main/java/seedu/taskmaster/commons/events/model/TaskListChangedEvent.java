package seedu.taskmaster.commons.events.model;

import seedu.taskmaster.commons.events.BaseEvent;
import seedu.taskmaster.model.ReadOnlyTaskMaster;

/** Indicates the AddressBook in the model has changed */
public class TaskListChangedEvent extends BaseEvent {

    public final ReadOnlyTaskMaster data;

    public TaskListChangedEvent(ReadOnlyTaskMaster data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "number of tasks " + data.getTaskOccurrenceList().size() + ", number of tags " + data.getTagList().size();
    }
}

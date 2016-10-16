package seedu.address.model;

import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskDateComponent;
import seedu.address.model.task.ReadOnlyTask;
import seedu.address.model.task.UniqueTaskList;
import seedu.address.model.task.UniqueTaskList.TaskNotFoundException;
import seedu.address.model.task.UniqueTaskList.TimeslotOverlapException;

import java.util.Date;
import java.util.Set;

import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskList newData);

    /** Returns the TaskList */
    ReadOnlyTaskList getTaskList();

    /** Deletes the given task. */
    void deleteTask(TaskDateComponent target) throws UniqueTaskList.TaskNotFoundException;

    /** Adds the given task 
     * @throws TimeslotOverlapException */
    void addTask(Task task) throws UniqueTaskList.DuplicateTaskException, TimeslotOverlapException;

    /** Returns the filtered task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();
    UnmodifiableObservableList<TaskDateComponent> getFilteredTaskComponentList();
    
    /** Updates the filter of the filtered task list to show all tasks */
    void updateFilteredListToShowAll();

    /** Updates the filter of the filtered task list to filter by the given keywords*/
    void updateFilteredTaskList(Set<String> keywords, Set<String> tags, Date startDate, Date endDate, Date deadline);
    
    /** Updates the file path for current storage manager of the model.*/
	void changeDirectory(String filePath);

	void archiveTask(TaskDateComponent target) throws TaskNotFoundException;

}

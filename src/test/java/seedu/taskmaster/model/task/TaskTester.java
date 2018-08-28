package seedu.taskmaster.model.task;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import seedu.taskmaster.commons.exceptions.IllegalValueException;
import seedu.taskmaster.model.task.RecurringType;
import seedu.taskmaster.model.task.Task;
import seedu.taskmaster.model.task.TaskType;
import seedu.taskmaster.model.task.stub.NameStub;
import seedu.taskmaster.model.task.stub.TaskDateStub;
import seedu.taskmaster.model.task.stub.TaskOccurrenceStub;
import seedu.taskmaster.model.task.stub.UniqueTagListStub;

//@@author A0135782Y
public class TaskTester {
    private static final int TASK_OCCURENCE_SIZE = 2;
    private Task task;
    private TaskTesterHelper helper;
    
    @Before
    public void setup() {
        helper = new TaskTesterHelper();
    }
    
    @Test
    public void create_task() throws Exception {
        task = helper.createFloatingTask();
        assertEquals(task.getTaskType(), TaskType.FLOATING);
        task = helper.createNonFloatingTask(RecurringType.NONE);
        assertEquals(task.getTaskType(), TaskType.NON_FLOATING);
    }
    
    @Test(expected=AssertionError.class)
    public void setRecurringType_floatingTask_throwAssert() throws Exception {
        task = helper.createFloatingTask();
        task.setRecurringType(RecurringType.DAILY);
    }

    @Test
    public void setRecurringType_successful() throws Exception {
        task = helper.createNonFloatingTask(RecurringType.NONE);
        task.setRecurringType(RecurringType.DAILY);
        assertEquals(task.getRecurringType(), RecurringType.DAILY);
    }

    @Test
    public void setTaskType_successful() throws Exception {
        task = helper.createNonFloatingTask(RecurringType.NONE);
        task.setTaskType(TaskType.COMPLETED);
        assertEquals("Task type should be mutated", task.getTaskType(), TaskType.COMPLETED);
    }

    @Test(expected=AssertionError.class)
    public void appendTaskComponent_toNonRecurringTask_notAllowed() throws Exception {
        task = helper.createNonFloatingTask(RecurringType.NONE);
        TaskOccurrenceStub toAppend = helper.createTaskOccurenceStub(task);
        task.appendRecurringDate(toAppend);
    }

    @Test
    public void getLastAppendedComponent_success() throws Exception {
        task = helper.createNonFloatingTask(RecurringType.DAILY);
        TaskOccurrenceStub toAppend = helper.createTaskOccurenceStub(task);
        task.appendRecurringDate(toAppend);
        TaskOccurrenceStub component = (TaskOccurrenceStub) task.getLastAppendedComponent();
        assertEquals("Task component just appended must be the last appended component", toAppend, component);
        assertEquals("Task occurrences should be properly appended", task.getTaskDateComponent().size(), TASK_OCCURENCE_SIZE);
    }
    
    class TaskTesterHelper {
        /**
         * Creates floating task
         */
        public Task createFloatingTask() throws IllegalValueException {
            return new Task(new NameStub(), new UniqueTagListStub());
        }
        
        /**
         * Creates non floating task
         */
        public Task createNonFloatingTask(RecurringType type) throws IllegalValueException {
            return new Task(new NameStub(), new UniqueTagListStub(),
                    new TaskDateStub(), new TaskDateStub(), type, Task.NO_RECURRING_PERIOD);
        }
        
        /**
         * Create task occurrence stub 
         */
        public TaskOccurrenceStub createTaskOccurenceStub(Task task) {
            return new TaskOccurrenceStub(task, new TaskDateStub(), new TaskDateStub());
        }
    }
}

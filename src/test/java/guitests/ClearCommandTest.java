package guitests;

import org.junit.Test;

import seedu.taskmaster.model.task.TaskOccurrence;
import seedu.taskmaster.testutil.TestUtil;

import static org.junit.Assert.assertTrue;

public class ClearCommandTest extends TaskMasterGuiTest {

    @Test
    public void clear() {
        commandBox.runCommand("list"); //switch to all tasks first

        TaskOccurrence[] taskComponents = TestUtil.convertTasksToDateComponents(td.getTypicalTasks());
        //verify a non-empty list can be cleared
        assertTrue(taskListPanel.isListMatching(taskComponents));
        assertClearCommandSuccess();

        //verify other commands can work after a clear command
        commandBox.runCommand(td.hoon.getAddFloatingCommand());
        assertTrue(taskListPanel.isListMatching(td.hoon.getTaskDateComponent().get(0)));
        commandBox.runCommand("delete 1");
        assertListSize(0);

        //verify clear command works when the list is empty
        assertClearCommandSuccess();
    }

    private void assertClearCommandSuccess() {
        commandBox.runCommand("clear");
        assertListSize(0);
        assertResultMessage("Task list has been cleared!");
    }
}

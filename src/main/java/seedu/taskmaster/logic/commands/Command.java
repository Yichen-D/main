package seedu.taskmaster.logic.commands;

import seedu.taskmaster.commons.core.EventsCenter;
import seedu.taskmaster.commons.core.Messages;
import seedu.taskmaster.commons.events.ui.FailedCommandAttemptedEvent;
import seedu.taskmaster.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.taskmaster.logic.UndoRedoManager;
import seedu.taskmaster.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be
 * executed.
 */
public abstract class Command {
    protected Model model;
    protected UndoRedoManager urManager;

    /**
     * Constructs a feedback message to summarise an operation that displayed a
     * listing of tasks.
     *
     * @param displaySize
     *            used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForTaskListShownSummary(int displaySize) {
        return String.format(Messages.MESSAGE_TASKS_LISTED_OVERVIEW, displaySize);
    }

    /**
     * Executes the command and returns the result message.
     *
     * @return feedback message of the operation result for display
     */
    public abstract CommandResult execute();

    /**
     * Provides any needed dependencies to the command. Commands making use of
     * any of these should override this method to gain access to the
     * dependencies.
     */
    public void setData(Model model) {
        this.model = model;
    }

    /**
     * Raises an event to indicate an attempt to execute an incorrect command
     */
    protected void indicateAttemptToExecuteIncorrectCommand() {
        EventsCenter.getInstance().post(new IncorrectCommandAttemptedEvent(this));
    }

    // @@author A0147967J
    /**
     * Assigns an undo/redo manager to the command to manage undo/redo
     * operation.
     */
    public void assignManager(UndoRedoManager urManager) {
        this.urManager = urManager;
    }

    /**
     * Raises an event to indicate an attempt to execute a failed command
     */
    protected void indicateAttemptToExecuteFailedCommand() {
        EventsCenter.getInstance().post(new FailedCommandAttemptedEvent(this));
    }
}

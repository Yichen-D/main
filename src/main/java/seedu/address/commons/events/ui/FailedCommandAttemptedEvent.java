package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;

/**
 * Indicates an attempt to execute an incorrect command
 */
public class FailedCommandAttemptedEvent extends BaseEvent {
	

    public FailedCommandAttemptedEvent(Command command) {
    }
    

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
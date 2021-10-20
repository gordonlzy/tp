package safeforhall.logic.commands.delete;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import safeforhall.commons.core.Messages;
import safeforhall.commons.core.index.Index;
import safeforhall.logic.commands.Command;
import safeforhall.logic.commands.CommandResult;
import safeforhall.logic.commands.exceptions.CommandException;
import safeforhall.model.Model;
import safeforhall.model.event.Event;

public class DeleteEventCommand extends Command {
    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the events identified by the index number used in the displayed event list.\n"
            + "Parameters: INDEXES (positive integers, separated by a space)\n"
            + "Example: " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_DELETE_EVENT_SUCCESS = "Deleted Events: \n%1$s";

    private final ArrayList<Index> targetIndexArray;

    public DeleteEventCommand(ArrayList<Index> targetIndexArray) {
        this.targetIndexArray = targetIndexArray;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownList = model.getFilteredEventList();
        List<Event> targetEventsArray = new ArrayList<>();
        String deletedEvents = "";
        int count = 0;

        for (Index targetIndex : targetIndexArray) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
            }
            Event eventToDelete = lastShownList.get(targetIndex.getZeroBased());
            targetEventsArray.add(eventToDelete);
        }

        for (Event eventToDelete : targetEventsArray) {
            deletedEvents += ((count + 1) + ".\t" + eventToDelete.getEventName() + "\n");
            model.deleteEvent(eventToDelete);
            count++;
        }

        return new CommandResult(String.format(MESSAGE_DELETE_EVENT_SUCCESS, deletedEvents));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteEventCommand // instanceof handles nulls
                && targetIndexArray.equals(((DeleteEventCommand) other).targetIndexArray)); // state check
    }
}

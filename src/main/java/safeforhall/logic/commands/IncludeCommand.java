package safeforhall.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import safeforhall.commons.core.index.Index;
import safeforhall.logic.commands.exceptions.CommandException;
import safeforhall.logic.parser.CliSyntax;
import safeforhall.model.Model;
import safeforhall.model.event.Event;
import safeforhall.model.event.ResidentList;
import safeforhall.model.person.Person;

/**
 * Adds a resident to an event.
 */
public class IncludeCommand extends Command {

    public static final String COMMAND_WORD = "include";
    public static final String PARAMETERS = "INDEX r/ROOM/NAME";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds residents to the given event.\n"
            + "Parameters: "
            + "INDEX "
            + CliSyntax.PREFIX_RESIDENTS + "ROOM/NAME \n"

            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + CliSyntax.PREFIX_RESIDENTS + "A101, A102, A103";

    public static final String MESSAGE_SUCCESS = "%s added to event %s";
    public static final String MESSAGE_EXCEED_CAPACITY = "Number of residents to add exceed event capacity";
    private final Index index;
    private final ResidentList residentList;

    /**
     * Creates an IncludeCommand to add the specified {@code EventName} and {@code InformationList}
     */
    public IncludeCommand(Index index, ResidentList residentList) {
        requireNonNull(index);
        requireNonNull(residentList);
        this.index = index;
        this.residentList = residentList;
    }

    /**
     * Checks if there are any person from {@code toAdd} who is already in {@code currentResidents}
     */
    public void checkForDuplicates(ArrayList<Person> toAdd, ArrayList<Person> currentResidents)
            throws CommandException {
        int foundDuplicate = 0;
        StringBuilder names = new StringBuilder();
        for (Person p : toAdd) {
            if (currentResidents.contains(p)) {
                if (foundDuplicate == 0) {
                    names.append(p.getName());
                } else {
                    names.append(", ").append(p.getName());
                }
                foundDuplicate++;
            }
        }
        if (foundDuplicate == 1) {
            throw new CommandException(names.toString() + " is already in this event");
        } else if (foundDuplicate > 1) {
            throw new CommandException(names.toString() + " are already in this event");
        }
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownList = model.getFilteredEventList();
        Event event;
        try {
            event = lastShownList.get(index.getZeroBased());
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("Index given is invalid");
        }
        if (residentList.isEmpty()) {
            throw new CommandException("No person with this information '" + residentList.getResidentsDisplay()
                    + "' could be found");
        }
        ArrayList<Person> toAdd = model.toPersonList(residentList);
        ArrayList<Person> currentResidents = model.getCurrentEventResidents(event.getResidentList());

        checkForDuplicates(toAdd, currentResidents);

        String combinedDisplayString = event.getCombinedDisplayString(toAdd);
        String combinedStorageString = event.getCombinedStorageString(toAdd);

        if (new ResidentList(combinedDisplayString,
                combinedStorageString).getResidents().size() > event.getCapacity().capacity) {
            throw new CommandException(MESSAGE_EXCEED_CAPACITY);
        }

        Event editedEvent = new Event(event.getEventName(), event.getEventDate(), event.getEventTime(),
                event.getVenue(), event.getCapacity(), new ResidentList(combinedDisplayString,
                combinedStorageString));
        model.setEvent(event, editedEvent);
        model.updateFilteredEventList(Model.PREDICATE_SHOW_ALL_EVENTS);

        String resultMsg = String.format(MESSAGE_SUCCESS, toAdd.stream()
                .map(p -> p.getName().toString()).reduce((x, y) -> x + ", " + y).get(), event.getEventName());
        return new CommandResult(resultMsg);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof IncludeCommand // instanceof handles nulls
                && index.equals(((IncludeCommand) other).index));
    }
}

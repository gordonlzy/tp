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
import safeforhall.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeletePersonCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the people identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEXES (positive integers, separated by a space)\n"
            + "Example: " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Residents: \n%1$s";

    private final ArrayList<Index> targetIndexArray;

    public DeletePersonCommand(ArrayList<Index> targetIndexArray) {
        this.targetIndexArray = targetIndexArray;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> targetResidentsArray = new ArrayList<>();
        String deletedResidents = "";
        int count = 0;

        for (Index targetIndex : targetIndexArray) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
            targetResidentsArray.add(personToDelete);
        }

        for (Person personToDelete : targetResidentsArray) {
            deletedResidents += ((count + 1) + ".\t" + personToDelete.getName() + "\n");
            model.deletePerson(personToDelete);
            count++;
        }

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, deletedResidents));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeletePersonCommand // instanceof handles nulls
                && targetIndexArray.equals(((DeletePersonCommand) other).targetIndexArray)); // state check
    }
}

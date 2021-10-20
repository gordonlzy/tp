package safeforhall.logic.parser;

import static safeforhall.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static safeforhall.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import safeforhall.logic.commands.ClearCommand;
import safeforhall.logic.commands.Command;
import safeforhall.logic.commands.EditCommand;
import safeforhall.logic.commands.ExitCommand;
import safeforhall.logic.commands.FindCommand;
import safeforhall.logic.commands.HelpCommand;
import safeforhall.logic.commands.IncludeCommand;
import safeforhall.logic.commands.ListCommand;
import safeforhall.logic.commands.ViewCommand;
import safeforhall.logic.commands.add.AddEventCommand;
import safeforhall.logic.commands.add.AddPersonCommand;
import safeforhall.logic.commands.delete.DeleteEventCommand;
import safeforhall.logic.commands.delete.DeletePersonCommand;
import safeforhall.logic.parser.add.AddEventCommandParser;
import safeforhall.logic.parser.add.AddPersonCommandParser;
import safeforhall.logic.parser.delete.DeleteEventCommandParser;
import safeforhall.logic.parser.delete.DeletePersonCommandParser;
import safeforhall.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @param isResidentTab indicates if the active tab is Resident or Model
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput, Boolean isResidentTab) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        default:
            if (isResidentTab) {
                return parseResidentCommand(commandWord, arguments);
            } else {
                return parseEventCommand(commandWord, arguments);
            }
        }
    }

    /**
     * Parses commands that have been input while the Resident tab is active.
     *
     * @param commandWord the main command word
     * @param arguments the provided arguments
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    private Command parseResidentCommand(String commandWord, String arguments) throws ParseException {
        switch (commandWord) {

        case AddPersonCommand.COMMAND_WORD:
            return new AddPersonCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeletePersonCommand.COMMAND_WORD:
            return new DeletePersonCommandParser().parse(arguments);

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommandParser().parse(arguments);

        case ViewCommand.COMMAND_WORD:
            return new ViewCommand();

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses commands that have been input while the Event tab is active.
     *
     * @param commandWord the main command word
     * @param arguments the provided arguments
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    private Command parseEventCommand(String commandWord, String arguments) throws ParseException {
        switch (commandWord) {

        case AddEventCommand.COMMAND_WORD:
            return new AddEventCommandParser().parse(arguments);

        case DeleteEventCommand.COMMAND_WORD:
            return new DeleteEventCommandParser().parse(arguments);

        /*case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case ViewCommand.COMMAND_WORD:
            return new ViewCommand();*/

        case IncludeCommand.COMMAND_WORD:
            return new IncludeCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}

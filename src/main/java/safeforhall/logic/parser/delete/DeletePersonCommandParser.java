package safeforhall.logic.parser.delete;

import static safeforhall.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;

import safeforhall.commons.core.index.Index;
import safeforhall.logic.commands.delete.DeletePersonCommand;
import safeforhall.logic.parser.Parser;
import safeforhall.logic.parser.ParserUtil;
import safeforhall.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeletePersonCommand object
 */
public class DeletePersonCommandParser implements Parser<DeletePersonCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeletePersonCommand
     * and returns a DeletePersonCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeletePersonCommand parse(String args) throws ParseException {
        try {
            ArrayList<Index> indexArray = ParserUtil.parseIndexes(args.trim().split(" "));
            return new DeletePersonCommand(indexArray);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeletePersonCommand.MESSAGE_USAGE), pe);
        }
    }

}

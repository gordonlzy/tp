package safeforhall.logic.parser.delete;

import static safeforhall.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static safeforhall.logic.parser.CommandParserTestUtil.assertParseFailure;
import static safeforhall.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static safeforhall.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import safeforhall.commons.core.index.Index;
import safeforhall.logic.commands.delete.DeletePersonCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeletePersonCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeletePersonCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeletePersonCommandParserTest {

    private DeletePersonCommandParser parser = new DeletePersonCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        ArrayList<Index> indexArray = new ArrayList<>();
        indexArray.add(INDEX_FIRST_PERSON);
        assertParseSuccess(parser, "1", new DeletePersonCommand(indexArray));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeletePersonCommand.MESSAGE_USAGE));
    }
}

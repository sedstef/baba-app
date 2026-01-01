package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.exception.InputParseException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InputParserTest {

    @Test
    public void parseAlphanumThrowsException() {
        //arrange
        String input = "-";

        //act
        InputParseException exception = assertThrows(InputParseException.class, () -> {
            InputParser.parseAlphanum(input);
        });

        //assert
        assertThat(exception.getMessage(), is("Only a-z, A-Z and 0-9 allowed"));
    }

    @Test
    public void parseLongThrowsException() {
        //arrange
        String input = "a";

        //act
        InputParseException exception = assertThrows(InputParseException.class, () -> {
            InputParser.parseLong(input);
        });

        //assert
        assertThat(exception.getMessage(), is("Invalid number input: a"));
    }
}

package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.exception.InputParseException;

import java.math.BigDecimal;

public class InputParser {
    public static String parseAlphanum(String input) throws InputParseException {
        if (!input.matches("[a-zA-Z0-9]+")) {
            throw new InputParseException("Only a-z, A-Z and 0-9 allowed");
        }
        return input;
    }

    public static int parseInt(String input) throws InputParseException {
        if (!input.matches("\\d+")) {
            throw new InputParseException("Invalid number input: " + input);
        }
        return Integer.parseInt(input);
    }

    public static long parseLong(String input) throws InputParseException {
        if (!input.matches("\\d+")) {
            throw new InputParseException("Invalid number input: " + input);
        }
        return Long.parseLong(input);
    }

    public static BigDecimal parseBigDecimal(String input) throws InputParseException {
        return BigDecimal.valueOf(parseDouble(input));
    }

    public static double parseDouble(String input) throws InputParseException {
        if (!input.matches("^(\\d+(\\.\\d+)?|\\.\\d+)$")) {
            throw new InputParseException("Invalid double input: " + input);
        }
        return Double.parseDouble(input);
    }


}

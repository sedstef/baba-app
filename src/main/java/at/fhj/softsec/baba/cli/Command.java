package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.exception.ApplicationException;
import at.fhj.softsec.baba.exception.InputParseException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

public interface Command {
    String[] name();

    default String toName() {
        return String.join(" ", name());
    }

    String description();

    default String usage() {
        return "";
    }

    default String getUsage() {
        return format("%s %s", toName(), usage());
    }

    void execute(String[] args, Application app, CliContext context) throws ApplicationException;

}

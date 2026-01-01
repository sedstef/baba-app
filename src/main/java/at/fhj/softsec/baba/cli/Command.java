package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.exception.InputParseException;

import java.io.IOException;

public interface Command {
    String name();

    String description();

    void execute(String[] args, Application app, CliContext context) throws InputParseException;

    interface CommandVisitor {

        void visit(Command command);
    }
}

package at.fhj.softsec.baba.cli;

import at.fhj.softsec.baba.Application;

import java.io.IOException;

public interface Command {
    String name();

    String description();

    void execute(String[] args, Application app, CliContext context) throws IOException;

    interface CommandVisitor{

        void visit(Command command);
    }
}

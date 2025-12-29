package at.fhj.softsec.baba.cli;

import java.io.IOException;

public interface Command {
    String name();

    String description();

    void execute(String[] args, CliContext context) throws IOException;

    interface CommandVisitor{

        void visit(Command command);
    }
}

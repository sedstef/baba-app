package at.fhj.softsec.baba;

import java.io.IOException;

public interface Command {
    String name();

    String description();

    default void execute(String[] args, CliContext context) throws IOException {
        // default: do nothing
    }
}

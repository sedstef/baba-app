package at.fhj.softsec.baba.cli;

import java.io.*;
import java.util.Objects;

public class CliContext {

    public static CliContext ofDefault() {
        return new CliContext(
            new BufferedReader(new InputStreamReader(System.in)),
            new PrintWriter(System.out, true)
        );
    }

    public final BufferedReader in;
    public final PrintWriter out;

    public CliContext(BufferedReader in, PrintWriter out) {
        this.in = Objects.requireNonNull(in, "in must not be null");
        this.out = Objects.requireNonNull(out, "out must not be null");
    }

    public String prompt(String message) {
        out.print(message);
        out.flush();
        try {
            return in.readLine();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to read input stdin", ex);
        }
    }

    public char[] promptPassword(String message) {
        Console console = System.console();
        if (console != null) {
            return console.readPassword(message);
        }
        // fallback (IDE / Gradle)
        return prompt(message).toCharArray();
    }
}

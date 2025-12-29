package at.fhj.softsec.baba.cli;

import java.io.*;
import java.util.Objects;

public class CliContext {
    public final BufferedReader in;
    public final PrintWriter out;

    public CliContext(BufferedReader in, PrintWriter out) {
        this.in = Objects.requireNonNull(in, "in must not be null");
        this.out = Objects.requireNonNull(out, "out must not be null");
    }

    public String prompt(String message) throws IOException {
        out.print(message);
        out.flush();
        return in.readLine();
    }

    public char[] promptPassword(String message) throws IOException {
        Console console = System.console();
        if (console != null) {
            return console.readPassword(message);
        }
        // fallback (IDE / Gradle)
        return prompt(message).toCharArray();
    }
}

package at.fhj.softsec.baba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;

class Zion {
    private final PrintStream out;
    private final BufferedReader reader;

    public static void defaultLoop() {
        new Zion(System.out,
                new BufferedReader(new java.io.InputStreamReader(System.in)))
                .promptLoop();
    }

    Zion(PrintStream out, BufferedReader reader) {
        this.out = Objects.requireNonNull(out, "out must not be null");
        this.reader = Objects.requireNonNull(reader, "reader must not be null");
    }

    void promptLoop() {
        do {
            try {
                out.print("Baba$: ");
                String input = reader.readLine();
                if ("help".equalsIgnoreCase(input.trim())) {
                    out.println("Available commands: help, exit");
                } else if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }
}

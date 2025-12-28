package at.fhj.softsec.baba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
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
                if("register".equalsIgnoreCase(input.trim())) {
                    out.print("username: ");
                    String username = reader.readLine();
                    out.print("password: ");
                    char[] password = reader.readLine().toCharArray();
                    out.print("verify password: ");
                    char[] verifyPassword = reader.readLine().toCharArray();

                    try {
                        new AuthService().register(username, password, verifyPassword);
                    }finally {
                        // Clear sensitive data
                        Arrays.fill(password, '\0');
                        Arrays.fill(verifyPassword, '\0');
                    }
                } else if ("help".equalsIgnoreCase(input.trim())) {
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

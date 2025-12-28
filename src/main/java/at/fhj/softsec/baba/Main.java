package at.fhj.softsec.baba;

import javax.swing.text.html.Option;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        printBanner();
        new Main(
                new PrintWriter(System.out, true),
                new BufferedReader(new InputStreamReader(System.in))
        ).promptLoop();
    }

    private static void printBanner() {
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final CliContext context;
    private final CommandRegistry root;

    Main(PrintWriter out, BufferedReader in) {
        context = new CliContext(in, out);

        root = new CommandRegistry()
                .register(new Command() {
                    @Override
                    public String name() {
                        return "help";
                    }

                    @Override
                    public String description() {
                        return "Show help information";
                    }

                    @Override
                    public void execute(String[] args, CliContext context) throws IOException {
                        out.println("Available commands: help, exit");
                    }
                })
                .register(new RegisterCommand())
                .register(new LoginCommand())
                .register(new LogoutCommand());
    }

    void promptLoop() {
        do {
            try {
                String prompt = Optional.ofNullable(StorageService.getInstance().getCurrentUsername())
                        .map(username -> username + "@BaBa> ")
                        .orElse("BaBa> ");
                String line = context.prompt(prompt);
                if (line == null || line.equals("exit")) break;

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 0) continue;
                root.execute(tokens, context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }
}
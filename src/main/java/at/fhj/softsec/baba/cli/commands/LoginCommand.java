package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.cli.Options;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.ApplicationException;

import java.util.Arrays;

import static at.fhj.softsec.baba.cli.InputParser.parseAlphanum;

public class LoginCommand implements Command {
    private final Options options = Options.builder()
            .withString("user name", value -> parseAlphanum(value))
            .build();

    @Override
    public String[] name() {
        return new String[]{"login"};
    }

    @Override
    public String description() {
        return "Login a already registered user";
    }

    @Override
    public String usage() {
        return options.getUsage();
    }

    @Override
    public void execute(String[] args, Application app, CliContext ctx) throws ApplicationException {
        options.parse(args);
        String username = options.getString("user name");
        char[] pw = ctx.promptPassword("Password: ");

        try {
            AuthenticatedUser user = app.auth().login(username, pw);
            app.session().login(user);
            ctx.out.println("Login successful.");
        } finally {
            // IMPORTANT: wipe passwords
            Arrays.fill(pw, '\0');
        }
    }
}

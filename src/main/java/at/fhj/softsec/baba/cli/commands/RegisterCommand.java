package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.exception.ApplicationException;

import java.util.Arrays;

import static at.fhj.softsec.baba.cli.InputParser.parseAlphanum;

public class RegisterCommand implements Command {

    @Override
    public String[] name() {
        return new String[]{"register"};
    }

    @Override
    public String description() {
        return "Add a new user";
    }

    @Override
    public String usage() {
        return "<user name>";
    }

    @Override
    public void execute(String[] args, Application app, CliContext ctx) throws ApplicationException {
        String username = parseAlphanum(args[0]);

        char[] pw1 = ctx.promptPassword("Password: ");
        char[] pw2 = ctx.promptPassword("Verify password: ");

        try {
            if (!Arrays.equals(pw1, pw2)) {
                ctx.out.println("Passwords do not match.");
                return;
            }
            app.auth().register(username, pw1);
            AuthenticatedUser authenticatedUser = app.auth().login(username, pw1);
            app.session().login(authenticatedUser);
        } finally {
            // IMPORTANT: wipe passwords
            Arrays.fill(pw1, '\0');
            Arrays.fill(pw2, '\0');
        }
    }

}

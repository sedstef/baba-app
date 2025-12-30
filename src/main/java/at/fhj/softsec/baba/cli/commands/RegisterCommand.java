package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;
import java.util.Arrays;

public class RegisterCommand implements Command {

    @Override
    public String name() {
        return "register";
    }

    @Override
    public String description() {
        return "Add a new user";
    }

    @Override
    public void execute(String[] args, Application app, CliContext ctx) throws IOException {

        //ctx.out.println("Creating new user (Ctrl+C to cancel)");

        String username = ctx.prompt("Username: ");

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

        //ctx.out.println("User '" + username + "' created.");
    }
}

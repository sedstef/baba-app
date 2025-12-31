package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;
import java.util.Arrays;

public class LoginCommand implements Command {

    @Override
    public String name() {
        return "login";
    }

    @Override
    public String description() {
        return "Login a already registered user";
    }

    @Override
    public void execute(String[] args, Application app, CliContext ctx) {

        //ctx.out.println("Creating new user (Ctrl+C to cancel)");

        String username = args[0];
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

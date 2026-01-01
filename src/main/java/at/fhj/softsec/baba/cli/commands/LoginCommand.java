package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;
import at.fhj.softsec.baba.exception.InputParseException;

import java.util.Arrays;

import static at.fhj.softsec.baba.cli.InputParser.parseAlphanum;

public class LoginCommand implements Command {

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
        return "<user name>";
    }

    @Override
    public void execute(String[] args, Application app, CliContext ctx) throws InputParseException {
        String username = parseAlphanum(args[0]);
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

package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

public class AccountCreateCommand implements Command {
    @Override
    public String name() {
        return "create";
    }

    @Override
    public String description() {
        return "Create a new account";
    }

    @Override
    public void execute(String[] args, Application app, CliContext context) throws IOException {
        AuthenticatedUser user = app.session().getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        app.accounts().create(user);

        context.out.println("Account 1 created.");
    }
}

package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

public class AccountCreateCommand extends AuthenticatedCommand {
    @Override
    public String name() {
        return "create";
    }

    @Override
    public String description() {
        return "Create a new account";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws IOException {
        app.accounts().create(user);

        context.out.println("Account 1 created.");
    }
}

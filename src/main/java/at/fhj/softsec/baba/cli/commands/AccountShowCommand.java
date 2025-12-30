package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.AuthenticatedCommand;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.domain.service.AuthenticatedUser;

import java.io.IOException;

public class AccountShowCommand extends AuthenticatedCommand {
    @Override
    public String name() {
        return "show";
    }

    @Override
    public String description() {
        return "Show a new account";
    }

    @Override
    protected void execute(String[] args, Application app, CliContext context, AuthenticatedUser user) throws IOException {
        context.out.println("Account 1 balance 0.");
    }
}

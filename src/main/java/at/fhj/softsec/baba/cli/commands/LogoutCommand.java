package at.fhj.softsec.baba.cli.commands;

import at.fhj.softsec.baba.Application;
import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Command;

import java.io.IOException;

public class LogoutCommand implements Command {

    @Override
    public String name() {
        return "logout";
    }

    @Override
    public String description() {
        return "Logout a user";
    }

    @Override
    public void execute(String[] args, Application app, CliContext ctx)  {
        app.session().logout();
        ctx.out.println("Logout successful.");
    }
}

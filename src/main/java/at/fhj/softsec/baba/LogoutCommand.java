package at.fhj.softsec.baba;

import java.io.IOException;
import java.util.Arrays;

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
    public void execute(String[] args, CliContext ctx) throws IOException {
        StorageService.getInstance().logout();
        ctx.out.println("Logout successful.");
    }
}

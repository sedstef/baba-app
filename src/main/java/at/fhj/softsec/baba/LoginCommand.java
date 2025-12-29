package at.fhj.softsec.baba;

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
    public void execute(String[] args, CliContext ctx) throws IOException {

        //ctx.out.println("Creating new user (Ctrl+C to cancel)");

        String username = args[1];
        char[] pw = ctx.promptPassword("Password: ");

        try {
            AuthService.getInstance().loginUser(username, pw);
            ctx.out.println("Login successful.");
        } finally {
            // IMPORTANT: wipe passwords
            Arrays.fill(pw, '\0');
        }
    }
}

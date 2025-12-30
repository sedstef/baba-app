package at.fhj.softsec.baba;

import at.fhj.softsec.baba.service.AuthService;
import at.fhj.softsec.baba.service.Session;

public interface Application {

    Session session();

    AuthService auth();
}

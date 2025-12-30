package at.fhj.softsec.baba;

import at.fhj.softsec.baba.domain.service.AccountsService;
import at.fhj.softsec.baba.domain.service.AuthService;
import at.fhj.softsec.baba.domain.service.Session;

public interface Application {

    Session session();

    AuthService auth();

    AccountsService accounts();
}

package at.fhj.softsec.baba;

import at.fhj.softsec.baba.domain.service.AccountService;
import at.fhj.softsec.baba.domain.service.AuthService;
import at.fhj.softsec.baba.domain.service.Session;
import at.fhj.softsec.baba.domain.service.TransferService;

public interface Application {

    Session session();

    AuthService auth();

    AccountService account();

    TransferService transfer();
}

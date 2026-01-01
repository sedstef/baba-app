package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.User;

import java.util.Collection;

public interface AccountService {
    OwnedAccount create(AuthenticatedUser authenticatedUser);

    OwnedAccount create(User user);

    Collection<OwnedAccount> retrieveAccounts(AuthenticatedUser authenticatedUser);

    OwnedAccount retrieveAccount(AuthenticatedUser authenticatedUser, Long accountNumber);

    void deleteAccount(AuthenticatedUser authenticatedUser, Long accountNumber);
}

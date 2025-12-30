package at.fhj.softsec.baba.domain.repository;

import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.model.User;

import java.util.Collection;

public interface AccountRepository {

    Long getNextAccountNumber();

    Account save(long accountNumber, User user);

    Collection<Account> retrieveAll(User user);
}

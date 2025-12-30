package at.fhj.softsec.baba.domain.repository;

import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.model.User;

import java.util.Collection;

public interface AccountRepository {

    Collection<Account> retrieveAll(User user);

    Account retrieveByNumber(User user, Long accountNumber);

    Long getNextAccountNumber();

    Account save(long accountNumber, User user);

}

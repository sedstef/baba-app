package at.fhj.softsec.baba.domain.repository;

import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.model.User;

import java.util.Collection;

public interface AccountRepository {

    Collection<Account> retrieveAll(User user);

    Account retrieveByUserAndNumber(User user, Long accountNumber);

    Account retrieveByNumber(Long accountNumber);

    Long getNextAccountNumber();

    Account save(Account account);

    void delete(User user, Long accountNumber);
}

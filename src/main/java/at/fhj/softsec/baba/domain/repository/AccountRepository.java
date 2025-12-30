package at.fhj.softsec.baba.domain.repository;

import at.fhj.softsec.baba.domain.model.Account;

public interface AccountRepository {

    Long getNextAccountNumber();

    void save(Account account);

}

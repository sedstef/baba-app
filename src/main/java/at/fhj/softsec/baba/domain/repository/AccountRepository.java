package at.fhj.softsec.baba.domain.repository;

import at.fhj.softsec.baba.domain.model.AccountView;
import at.fhj.softsec.baba.domain.model.ForeignAccount;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.User;

import java.util.Collection;

public interface AccountRepository {

    Collection<OwnedAccount> retrieveAll(User user);

    OwnedAccount retrieveByUserAndNumber(User user, Long accountNumber);

    ForeignAccount retrieveByNumber(Long accountNumber);

    Long getNextAccountNumber();

    OwnedAccount create(User user, Long accountNumber);

    <T extends AccountView> T save(T account);

    void delete(User user, Long accountNumber);
}

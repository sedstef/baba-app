package at.fhj.softsec.baba.domain.model;

import java.math.BigDecimal;
import java.util.Collection;

public interface OwnedAccount extends AccountView {

    String getUserId();

    BigDecimal getBalance();

    void deposit(BigDecimal amount);

    void withdraw(BigDecimal amount);

    void transferOut(AccountView target, BigDecimal amount);

    Collection<Movement> getMovements();
}

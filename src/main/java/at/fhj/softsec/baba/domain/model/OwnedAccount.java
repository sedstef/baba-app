package at.fhj.softsec.baba.domain.model;

import at.fhj.softsec.baba.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Collection;

public interface OwnedAccount extends AccountView {

    String getUserId();

    BigDecimal getBalance();

    void deposit(BigDecimal amount);

    void withdraw(BigDecimal amount) throws InsufficientFundsException;

    void transferOut(AccountView target, BigDecimal amount) throws InsufficientFundsException;

    Collection<Movement> getMovements();
}

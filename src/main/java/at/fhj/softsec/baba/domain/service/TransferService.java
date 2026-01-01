package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.domain.model.OwnedAccount;

import java.math.BigDecimal;

public interface TransferService {
    OwnedAccount deposit(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount);

    OwnedAccount withdrawal(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount);

    OwnedAccount transfer(AuthenticatedUser authenticatedUser, Long sourceNumber, Long targetNumber, BigDecimal amount);
}

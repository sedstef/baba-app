package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.AuthenticatedUser;
import at.fhj.softsec.baba.domain.model.ForeignAccount;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.AccountRepository;
import at.fhj.softsec.baba.domain.repository.UserRepository;

import java.math.BigDecimal;

public class TransferServiceImpl implements TransferService {

    private final AccountRepository repository;
    private final UserRepository userRepository;

    public TransferServiceImpl(AccountRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public OwnedAccount deposit(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        OwnedAccount ownedAccount = repository.retrieveByUserAndNumber(user, accountNumber);
        ownedAccount.deposit(amount);
        return repository.save(ownedAccount);
    }

    @Override
    public OwnedAccount withdrawal(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        OwnedAccount ownedAccount = repository.retrieveByUserAndNumber(user, accountNumber);
        ownedAccount.withdraw(amount);
        return repository.save(ownedAccount);
    }

    @Override
    public OwnedAccount transfer(AuthenticatedUser authenticatedUser, Long sourceNumber, Long targetNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        OwnedAccount sourceAccount = repository.retrieveByUserAndNumber(user, sourceNumber);
        ForeignAccount targetAccount = repository.retrieveByNumber(targetNumber);

        sourceAccount.transferOut(targetAccount, amount);
        targetAccount.transferIn(sourceAccount, amount);

        repository.save(sourceAccount);
        repository.save(targetAccount);

        return sourceAccount;
    }
}

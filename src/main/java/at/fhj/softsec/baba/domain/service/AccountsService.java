package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.ForeignAccount;
import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.AccountRepository;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Collection;

public class AccountsService {
    private final AccountRepository repository;
    private final UserRepository userRepository;

    public AccountsService(AccountRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public OwnedAccount create(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return create(user);
    }

    public OwnedAccount create(User user) {
        Long accountNumber = repository.getNextAccountNumber();
        return repository.create(user, accountNumber);
    }

    public Collection<OwnedAccount> retrieveAccounts(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return repository.retrieveAll(user);
    }

    public OwnedAccount retrieveAccount(AuthenticatedUser authenticatedUser, Long accountNumber) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return repository.retrieveByUserAndNumber(user, accountNumber);
    }

    public void deleteAccount(AuthenticatedUser authenticatedUser, Long accountNumber) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        repository.delete(user, accountNumber);
    }

    public OwnedAccount deposit(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        OwnedAccount ownedAccount = repository.retrieveByUserAndNumber(user, accountNumber);
        ownedAccount.deposit(amount);
        return repository.save(ownedAccount);
    }

    public OwnedAccount withdrawal(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        OwnedAccount ownedAccount = repository.retrieveByUserAndNumber(user, accountNumber);
        ownedAccount.withdraw(amount);
        return repository.save(ownedAccount);
    }

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

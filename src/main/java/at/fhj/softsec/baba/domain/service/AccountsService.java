package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.AccountRepository;
import at.fhj.softsec.baba.domain.model.Account;
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

    public Account create(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return create(user);
    }

    public Account create(User user) {
        Long accountNumber = repository.getNextAccountNumber();
        return repository.save(new Account(accountNumber, user.userId()));
    }

    public Collection<Account> retrieveAccounts(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return repository.retrieveAll(user);
    }

    public Account retrieveAccount(AuthenticatedUser authenticatedUser, Long accountNumber) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return repository.retrieveByUserAndNumber(user, accountNumber);
    }

    public void deleteAccount(AuthenticatedUser authenticatedUser, Long accountNumber) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        repository.delete(user, accountNumber);
    }

    public Account deposit(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        return makeMovement(user, accountNumber, amount);
    }

    public Account withdrawal(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        return makeMovement(user, accountNumber, amount.negate());
    }

    private Account makeMovement(User user, Long accountNumber, BigDecimal amount) {
        Account account = repository.retrieveByUserAndNumber(user, accountNumber);
        account.addMovement(amount);
        return repository.save(account);
    }

    public Account transfer(AuthenticatedUser authenticatedUser, Long sourceNumber, Long targetNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        Account sourceAccount = makeMovement(user, sourceNumber, amount.negate());
        Account targetAccount = repository.retrieveByNumber(targetNumber);
        targetAccount.addMovement(amount);
        repository.save(targetAccount);

        return sourceAccount;
    }
}

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
        Long accountNumber = repository.getNextAccountNumber();
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
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
        return repository.retrieveByNumber(user, accountNumber);
    }

    public void deleteAccount(AuthenticatedUser authenticatedUser, Long accountNumber) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        repository.delete(user, accountNumber);
    }

    public Account deposit(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        return makeMovement(authenticatedUser, accountNumber, amount);
    }

    public Account withdrawal(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        return makeMovement(authenticatedUser, accountNumber, amount.negate());
    }

    private Account makeMovement(AuthenticatedUser authenticatedUser, Long accountNumber, BigDecimal amount) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();

        Account account = repository.retrieveByNumber(user, accountNumber);
        account.addMovement(amount);
        return repository.save(account);
    }
}

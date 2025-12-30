package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.AccountRepository;
import at.fhj.softsec.baba.domain.model.Account;
import at.fhj.softsec.baba.domain.repository.UserRepository;

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
        return repository.save(accountNumber, user);
    }

    public Collection<Account> retrieveAcounts(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow();
        return repository.retrieveAll(user);
    }
}

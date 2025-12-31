package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.model.User;
import at.fhj.softsec.baba.domain.repository.AccountRepository;
import at.fhj.softsec.baba.domain.model.OwnedAccount;
import at.fhj.softsec.baba.domain.repository.UserRepository;

import java.util.Collection;

public class AccountService {

    private final AccountRepository repository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository repository, UserRepository userRepository) {
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

}

package at.fhj.softsec.baba.domain.service;

import at.fhj.softsec.baba.domain.repository.AccountRepository;
import at.fhj.softsec.baba.domain.model.Account;

public class AccountsService {
    private final AccountRepository repository;

    public AccountsService(AccountRepository repository) {
        this.repository = repository;
    }

    public void create(AuthenticatedUser user) {
        // Implementation for creating an account for the authenticated user
        Long accountNumber = repository.getNextAccountNumber();
        repository.save(new Account(accountNumber, user.getUserId()));
    }
}

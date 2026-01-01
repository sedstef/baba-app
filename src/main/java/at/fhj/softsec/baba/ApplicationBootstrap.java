package at.fhj.softsec.baba;

import at.fhj.softsec.baba.domain.service.*;
import at.fhj.softsec.baba.persistence.AccountFileRepository;
import at.fhj.softsec.baba.persistence.Storage;
import at.fhj.softsec.baba.persistence.UserFileRepository;

import javax.crypto.SecretKey;
import java.nio.file.Path;

public class ApplicationBootstrap {

    public static Application create(Path dataDir, SecretKey secretKey) {
        final Session session = new SessionImpl();

        Storage storage = new Storage(dataDir, secretKey);

        final AuthService authService = new AuthServiceImpl(new UserFileRepository(storage));
        final AccountService accountService = new AccountServiceImpl(
                new AccountFileRepository(storage),
                new UserFileRepository(storage)
        );

        final TransferService transferService = new TransferServiceImpl(
                new AccountFileRepository(storage),
                new UserFileRepository(storage)
        );

        return new Application() {

            @Override
            public Session session() {
                return session;
            }

            @Override
            public AuthService auth() {
                return authService;
            }

            @Override
            public AccountService account() {
                return accountService;
            }

            @Override
            public TransferService transfer() {
                return transferService;
            }
        };
    }
}

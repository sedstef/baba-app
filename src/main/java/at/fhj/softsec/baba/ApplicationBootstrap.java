package at.fhj.softsec.baba;

import at.fhj.softsec.baba.domain.audit.AuditInvocationHandler;
import at.fhj.softsec.baba.domain.service.*;
import at.fhj.softsec.baba.persistence.AccountFileRepository;
import at.fhj.softsec.baba.persistence.Storage;
import at.fhj.softsec.baba.persistence.UserFileRepository;

import javax.crypto.SecretKey;
import java.lang.reflect.Proxy;
import java.nio.file.Path;

public class ApplicationBootstrap {

    private final Path auditlog;

    public static Application create(Path dataDir, SecretKey secretKey) {
        return new ApplicationBootstrap(dataDir, secretKey).createApplication();
    }

    private final Path dataDir;
    private final SecretKey secretKey;

    private ApplicationBootstrap(Path dataDir, SecretKey secretKey) {
        this.dataDir = dataDir;
        this.secretKey = secretKey;

        auditlog = dataDir.resolve("audit.log");
    }

    private Application createApplication() {
        final Session session = new SessionImpl();

        Storage storage = new Storage(dataDir, secretKey);

        AuthService authService = auditProxy(AuthService.class,
                new AuthServiceImpl(new UserFileRepository(storage)));

        final AccountService accountService = auditProxy(AccountService.class,
                new AccountServiceImpl(
                        new AccountFileRepository(storage),
                        new UserFileRepository(storage)
                ));

        final TransferService transferService = auditProxy(TransferService.class,
                new TransferServiceImpl(
                        new AccountFileRepository(storage),
                        new UserFileRepository(storage)
                ));

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


    @SuppressWarnings("unchecked")
    private <T> T auditProxy(Class<T> interfaceType, T target) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new AuditInvocationHandler(target, auditlog)
        );
    }
}

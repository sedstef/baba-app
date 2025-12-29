package at.fhj.softsec.baba.storage;

import at.fhj.softsec.baba.service.InvalidPasswordException;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

public class Storage {
    private final static Storage INSTANCE = new Storage();

    public static Storage getInstance() {
        return INSTANCE;
    }

    private boolean locked = true;
    private SecretKey secretKey;

    public boolean isLocked() {
        return locked;
    }

    public void unlock(char[] masterPassword) throws InvalidPasswordException {
        try {
            secretKey = MasterKeyLoader.load(masterPassword);
            locked = false;
        } catch (GeneralSecurityException e) {
            throw new InvalidPasswordException("Invalid master password");
        }
    }

    public UserRepository getUserRepository() {
        if (isLocked()) {
            throw new IllegalStateException("Storage is locked");
        }
        return new UserRepository(secretKey);
    }

    public static class UserRepository {
        private final SecretKey secretKey;

        UserRepository(SecretKey secretKey) {
            this.secretKey = secretKey;
        }
    }
}

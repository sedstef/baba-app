package at.fhj.softsec.baba.security;

import at.fhj.softsec.baba.exception.MasterKeyUnlockException;
import at.fhj.softsec.baba.exception.StorageAccessException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

public class MasterKeyLoader {

    private static final String KEY_FILE = "master.key";

    public static SecretKey loadMasterKey(Path data, char[] password) throws StorageAccessException, MasterKeyUnlockException {
        try {
            Path keyFile = data.resolve(KEY_FILE);
            byte[] keyBytes;
            if (Files.exists(keyFile)) {
                keyBytes = loadKey(keyFile, password);
            } else {
                keyBytes = createKey(keyFile, password);
            }
            return new SecretKeySpec(keyBytes, CryptoUtils.CIPHER_ALGORITHM);
        } catch (IOException | GeneralSecurityException e) {
            throw new StorageAccessException("Cannot create master key", e);
        }
    }

    private static byte[] createKey(Path file, char[] password) throws IOException, GeneralSecurityException {
        byte[] salt = CryptoUtils.generateSalt();
        byte[] keyBytes = CryptoUtils.pbkdf2(password, salt, CryptoUtils.ITERATIONS, CryptoUtils.KEY_LENGTH);

        String record = CryptoUtils.ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(keyBytes);

        Files.createDirectories(file.getParent());
        Files.writeString(file, record);
        return keyBytes;
    }

    private static byte[] loadKey(Path file, char[] password) throws IOException, GeneralSecurityException, StorageAccessException, MasterKeyUnlockException {
        String[] parts = Files.readString(file).split(":");
        if (parts.length != 3) {
            throw new StorageAccessException("Invalid key file");
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] storedKey = Base64.getDecoder().decode(parts[2]);

        byte[] keyBytes = CryptoUtils.pbkdf2(password, salt, iterations, CryptoUtils.KEY_LENGTH);

        // Constant-time verification
        if (!MessageDigest.isEqual(storedKey, keyBytes)) {
            throw new MasterKeyUnlockException("Failed to unlock secure storage");
        }
        return keyBytes;
    }

    private MasterKeyLoader() {
        // utility class
    }

}

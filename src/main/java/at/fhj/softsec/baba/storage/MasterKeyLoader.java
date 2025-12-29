package at.fhj.softsec.baba.storage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public final class MasterKeyLoader {

    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_ALGORITHM = "AES";
    private static final int ITERATIONS = 200_000;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    private static final Path SALT_FILE = Path.of("data", "master.salt");

    private MasterKeyLoader() {
        // utility class
    }

    public static SecretKey load(char[] masterPassword) throws GeneralSecurityException {
        byte[] salt = loadOrCreateSalt();
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
        PBEKeySpec spec = new PBEKeySpec(
                masterPassword,
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, CIPHER_ALGORITHM);
    }

    private static byte[] loadOrCreateSalt() {
        try {
            if (Files.exists(SALT_FILE)) {
                return Files.readAllBytes(SALT_FILE);
            }

            Files.createDirectories(SALT_FILE.getParent());

            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            Files.write(SALT_FILE, salt);
            return salt;

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load or create master salt", e);
        }
    }
}

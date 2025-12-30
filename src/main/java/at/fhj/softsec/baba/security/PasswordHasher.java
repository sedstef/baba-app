package at.fhj.softsec.baba.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
        // utility class
    }

    /**
     * Hashes a password for storage.
     */
    public static String hash(char[] password) {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

        return String.format(
                "%s:%d:%s:%s",
                ALGORITHM,
                ITERATIONS,
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash)
        );
    }

    /**
     * Verifies a password against a stored hash.
     */
    public static boolean verify(char[] password, String stored) {

        String[] parts = stored.split(":");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid password hash format");
        }

        String algorithm = parts[0];
        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

        if (!ALGORITHM.equals(algorithm)) {
            throw new IllegalStateException("Unsupported hash algorithm");
        }

        byte[] actualHash = pbkdf2(
                password,
                salt,
                iterations,
                expectedHash.length * 8
        );

        Arrays.fill(password, '\0');

        // Constant-time comparison
        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    // ----------------- internal helpers -----------------

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return salt;
    }

    private static byte[] pbkdf2(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength
    ) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password,
                    salt,
                    iterations,
                    keyLength
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }
}

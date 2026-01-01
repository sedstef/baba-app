package at.fhj.softsec.baba.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class CryptoUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    static final String CIPHER_ALGORITHM = "AES";
    static final int ITERATIONS = 200_000;
    static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    private static final SecureRandom RANDOM = new SecureRandom();

    private CryptoUtils() {
        // utility class
    }

    /**
     * Hashes a password for storage.
     */
    public static String hash(char[] password) {
        try {
            byte[] salt = generateSalt();
            byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

            return String.format(
                    "%s:%d:%s:%s",
                    ALGORITHM,
                    ITERATIONS,
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(hash)
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     */
    public static boolean verifyHash(char[] password, String stored) {
        try {
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
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to verify password hash", e);
        }
    }

    static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return salt;
    }

    static byte[] pbkdf2(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength
    ) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                keyLength
        );
        return SecretKeyFactory.getInstance(ALGORITHM)
                .generateSecret(spec).getEncoded();
    }


}

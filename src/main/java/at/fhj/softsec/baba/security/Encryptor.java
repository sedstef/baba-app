package at.fhj.softsec.baba.security;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.*;

public class Encryptor {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] encrypt(byte[] plaintext, SecretKey secretKey) throws GeneralSecurityException {

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);

        byte[] ciphertext = getCipher(Cipher.ENCRYPT_MODE, secretKey, iv)
                .doFinal(plaintext);

        // Write salt + iv + ciphertext
        byte[] encrypted = new byte[salt.length + iv.length + ciphertext.length];
        System.arraycopy(salt, 0, encrypted, 0, salt.length);
        System.arraycopy(iv, 0, encrypted, salt.length, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, salt.length + iv.length, ciphertext.length);

        return encrypted;
    }

    public static byte[] decrypt(byte[] fileData, SecretKey secretKey) throws GeneralSecurityException {
        byte[] iv = Arrays.copyOfRange(fileData, 16, 28);
        byte[] ciphertext = Arrays.copyOfRange(fileData, 28, fileData.length);

        return getCipher(Cipher.DECRYPT_MODE, secretKey, iv)
                .doFinal(ciphertext);
    }

    private static Cipher getCipher(int encryptMode, SecretKey secretKey, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(
                encryptMode,
                secretKey,
                new GCMParameterSpec(128, iv));
        return cipher;
    }
}


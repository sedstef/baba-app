package at.fhj.softsec.baba.security;

import at.fhj.softsec.baba.exception.MasterKeyUnlockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.crypto.SecretKey;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageEncryptorTest {

    @Test
    public void testDecrypt(@TempDir Path tmpDir) throws Exception{
        // arrange
        String secret = "secret";
        SecretKey masterKey = MasterKeyLoader.loadMasterKey(tmpDir, secret.toCharArray());
        byte[] plaintext = "test".getBytes();
        StorageEncryptor.encrypt(masterKey,tmpDir.resolve("file.enc"), plaintext);

        // act
        byte[] result = StorageEncryptor.decrypt(masterKey, tmpDir.resolve("file.enc"));

        //assert
        assertThat(result, is(plaintext));
    }


}

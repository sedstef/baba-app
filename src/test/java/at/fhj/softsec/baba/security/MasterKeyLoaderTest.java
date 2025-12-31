package at.fhj.softsec.baba.security;

import at.fhj.softsec.baba.exception.MasterKeyUnlockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.crypto.SecretKey;
import java.nio.file.Path;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.platform.commons.util.Preconditions.notNull;

public class MasterKeyLoaderTest {

    @Test
    public void testCreateNewMasterKey(@TempDir Path tmpDir) throws Exception{
        // arrange
        String secret = "secret";

        // act
        SecretKey resultKey = MasterKeyLoader.loadMasterKey(tmpDir, secret.toCharArray());

        //assert
        assertThat(resultKey, is(notNullValue()));
        assertThat(tmpDir.resolve("master.key").toFile(), anExistingFile());
    }

    @Test
    public void testLoadExistingMasterKey(@TempDir Path tmpDir) throws Exception{
        // arrange
        String secret = "secret";
        SecretKey secretKey = MasterKeyLoader.loadMasterKey(tmpDir, secret.toCharArray());

        // act
        SecretKey resultKey = MasterKeyLoader.loadMasterKey(tmpDir, secret.toCharArray());

        //assert
        assertThat(resultKey, is(notNullValue()));
        assertThat(resultKey, is(secretKey));
        assertThat(tmpDir.resolve("master.key").toFile(), anExistingFile());
    }

    @Test
    public void testLoadExistingMasterKeyWrongPassword(@TempDir Path tmpDir) throws Exception{
        // arrange
        SecretKey secretKey = MasterKeyLoader.loadMasterKey(tmpDir, "secret".toCharArray());

        // act
        MasterKeyUnlockException result = assertThrows(MasterKeyUnlockException.class, () ->
            MasterKeyLoader.loadMasterKey(tmpDir, "wrong-ecret".toCharArray())
        );;

        //assert
        assertThat(result, is(notNullValue()));
    }
}

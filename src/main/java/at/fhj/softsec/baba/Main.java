package at.fhj.softsec.baba;

import at.fhj.softsec.baba.cli.CliContext;
import at.fhj.softsec.baba.cli.Zion;
import at.fhj.softsec.baba.exception.MasterKeyUnlockException;
import at.fhj.softsec.baba.exception.StorageAccessException;
import at.fhj.softsec.baba.security.MasterKeyLoader;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class Main {

    static Path DATA_DIR = Path.of("data");

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (StorageAccessException e) {
            System.err.println("Internal storage error.");
            System.exit(1);
        }
    }

    private void run() throws StorageAccessException {
        CliContext cliContext = CliContext.ofDefault();
        printBanner(cliContext);

        SecretKey secretKey = promptMasterKey(cliContext);
        Application app = ApplicationBootstrap.create(Path.of("data"), secretKey);

        new Zion(app, cliContext)
                .promptLoop();
    }

    private void printBanner(CliContext cliContext) {
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            cliContext.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey promptMasterKey(CliContext cliContext) throws StorageAccessException {
        while (true) {
            char[] masterPassword = cliContext.promptPassword("Enter master password to unlock BaBa:");
            try {
                return MasterKeyLoader.loadMasterKey(DATA_DIR, masterPassword);
            } catch (MasterKeyUnlockException e) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                cliContext.out.println("Invalid master password, try again.");
            } finally {
                Arrays.fill(masterPassword, '\0');
            }
        }
    }

}
package at.fhj.softsec.baba;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        printBanner();
        Zion.defaultLoop();
    }

    private static void printBanner() {
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
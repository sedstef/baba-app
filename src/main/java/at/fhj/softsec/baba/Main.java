package at.fhj.softsec.baba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        printBanner();
        promptLoop();
    }

    private static void printBanner() {
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void promptLoop() {
        do {
            try {
                System.out.print("Baba$: ");
                String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }

}
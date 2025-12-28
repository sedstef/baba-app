package at.fhj.softsec.baba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        printBanner();
        promptLoop(new BufferedReader(new InputStreamReader(System.in)));
    }

    private static void printBanner() {
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* package private cause of tests */
    /*default*/ static void promptLoop(BufferedReader reader) {
        do {
            try {
                System.out.print("Baba$: ");
                String input = reader.readLine();
                if("help".equalsIgnoreCase(input.trim())) {
                    System.out.println("Available commands: help, exit");
                }else if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }

}
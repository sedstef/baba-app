package at.fhj.softsec.baba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {

        try {
            String bannerText = readBannerText();
            System.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        do {
            try {
                System.out.print("Baba$: ");
                String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
                System.out.printf("You entered: %s%n", input);
                if("exit".equalsIgnoreCase(input.trim())){
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }

    private static String readBannerText() throws IOException{
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
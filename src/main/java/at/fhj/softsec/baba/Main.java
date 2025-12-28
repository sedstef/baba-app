package at.fhj.softsec.baba;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {

        try {
            String bannerText = readBannerText();
            System.out.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readBannerText() throws IOException{
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
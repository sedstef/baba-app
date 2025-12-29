package at.fhj.softsec.baba;

import at.fhj.softsec.baba.cli.Zion;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        PrintWriter printWriter = new PrintWriter(System.out, true);
        printBanner(printWriter);
        new Zion(
                printWriter,
                new BufferedReader(new InputStreamReader(System.in))
        ).promptLoop();
    }

    private static void printBanner(PrintWriter writer) {
        try (InputStream is = Main.class.getResourceAsStream("/banner.txt")) {
            String bannerText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            writer.println(bannerText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
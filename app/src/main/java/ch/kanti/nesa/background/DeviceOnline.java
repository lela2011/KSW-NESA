package ch.kanti.nesa.background;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DeviceOnline {
    public static boolean check() {

        try {
            Document wikipedia = Jsoup.connect("https://www.wikipedia.org/")
                    .method(Connection.Method.GET)
                    .followRedirects(false)
                    .execute()
                    .parse();

            Elements onlineCheck = wikipedia.select(".central-textlogo__image");
            if(onlineCheck.size() != 0) {
                return onlineCheck.get(0).text().contains("Wikipedia");
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

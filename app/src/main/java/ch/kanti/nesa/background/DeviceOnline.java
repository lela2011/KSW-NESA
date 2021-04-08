package ch.kanti.nesa.background;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

public class DeviceOnline {
    public static boolean check() {
        /*try {
            URL url = new URL("http://kantiwattwil.ch");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            Log.e("ConnectionError", "Error: ", e);
            return false;
        }*/

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

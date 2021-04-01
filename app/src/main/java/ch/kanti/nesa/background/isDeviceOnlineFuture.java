package ch.kanti.nesa.background;

import android.telecom.ConnectionRequest;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class isDeviceOnlineFuture implements Callable<Boolean> {
    @Override
    public Boolean call() {
        try {
            URL url = new URL("http://www.google.com/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            /*urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();*/
            return (connection.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e("ConnectionError", "Error: ", e);
            return false;
        }
    }
}

package ch.kanti.nesa.background;

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
            return (connection.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e("ConnectionError", "Error: ", e);
            return false;
        }
    }
}

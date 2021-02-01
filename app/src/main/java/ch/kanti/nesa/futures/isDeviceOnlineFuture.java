package ch.kanti.nesa.futures;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class isDeviceOnlineFuture implements Callable<Boolean> {
    @Override
    public Boolean call() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com/").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e("ConnectionError", "Error: ", e);
            return false;
        }
    }
}

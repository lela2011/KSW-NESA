package ch.kanti.nesa.scrapers;

import ch.kanti.nesa.CookieAndAuth;
import ch.kanti.nesa.SplashActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class CookieAndAuthScraper implements Callable<CookieAndAuth> {

    @Override
    public CookieAndAuth call() throws Exception {
        Connection.Response loginResponse = Jsoup.connect(SplashActivity.LOGIN_FORM_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:83.0) Gecko/20100101 Firefox/83.0")
                .method(Connection.Method.GET)
                .execute();

        HashMap<String, String> cookies = new HashMap<>(loginResponse.cookies());
        String authToken = loginResponse.parse().select("div.mdl-cell:nth-child(4) > input:nth-child(3)").first().attr("value"); //div.mdl-cell:nth-child(3) > input:nth-child(3)

        return new CookieAndAuth(cookies, authToken);
    }
}

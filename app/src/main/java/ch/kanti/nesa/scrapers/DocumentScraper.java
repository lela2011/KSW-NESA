package ch.kanti.nesa.scrapers;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.CookieAndAuth;
import ch.kanti.nesa.SplashActivity;
import ch.kanti.nesa.tables.User;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocumentScraper {

    static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static Document getMainPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://test.nesa-sg.ch/schulung/ksw/index.php?pageid=1", cookies, formData);
    }

    public static Document getMarkPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://test.nesa-sg.ch/schulung/ksw/index.php?pageid=21311", cookies, formData);
    }

    public static Document getAbsencesPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://test.nesa-sg.ch/schulung/ksw/index.php?pageid=21111", cookies, formData);
    }

    public static Document getBankPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://test.nesa-sg.ch/schulung/ksw/index.php?pageid=21411", cookies, formData);
    }

    public static Document getEmailPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://test.nesa-sg.ch/schulung/ksw/index.php?pageid=22500", cookies, formData);
    }

    public static Document scrapePage(String url, HashMap<String, String> cookies, HashMap<String, String> formData) {
        Future<Document> pageFuture = executorService.submit(new PageScraper(cookies, formData, url));
        try {
            return pageFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CookieAndAuth cookiesAndAuth() {
        Future<CookieAndAuth> login = executorService.submit(new CookieAndAuthScraper());
        try {
            return login.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}

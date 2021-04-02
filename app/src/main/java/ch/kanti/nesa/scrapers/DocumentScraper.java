package ch.kanti.nesa.scrapers;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.App;
import ch.kanti.nesa.objects.CookieAndAuth;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocumentScraper {

    static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static Document getMainPage() {
        String username = App.sharedPreferences.getString("username", "");
        String password = App.sharedPreferences.getString("password", "");
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, App.usernameKey));
        formData.put("passwort", AES.decrypt(password, App.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=1", cookies, formData);
    }

    public static Document getMarkPage() {
        String username = App.sharedPreferences.getString("username", "");
        String password = App.sharedPreferences.getString("password", "");
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, App.usernameKey));
        formData.put("passwort", AES.decrypt(password, App.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21311", cookies, formData);
    }

    public static Document getAbsencesPage() {
        String username = App.sharedPreferences.getString("username", "");
        String password = App.sharedPreferences.getString("password", "");
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, App.usernameKey));
        formData.put("passwort", AES.decrypt(password, App.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21111", cookies, formData);
    }

    public static Document getBankPage() {
        String username = App.sharedPreferences.getString("username", "");
        String password = App.sharedPreferences.getString("password", "");
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, App.usernameKey));
        formData.put("passwort", AES.decrypt(password, App.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21411", cookies, formData);
    }

    public static Document getEmailPage() {
        String username = App.sharedPreferences.getString("username", "");
        String password = App.sharedPreferences.getString("password", "");
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, App.usernameKey));
        formData.put("passwort", AES.decrypt(password, App.passwordKey));
        formData.put("loginhash", authToken);

        return scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=22500", cookies, formData);
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

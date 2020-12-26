package com.example.nesa.scrapers;

import com.example.nesa.AES;
import com.example.nesa.CookieAndAuth;
import com.example.nesa.SplashActivity;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.concurrent.AbstractExecutorService;
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

        Document mainPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=1", cookies, formData);
        return mainPage;
    }

    public static Document getMarkPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        Document markPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21311", cookies, formData);
        return markPage;
    }

    public static Document getAbsencesPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        Document absencesPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21111", cookies, formData);
        return absencesPage;
    }

    public static Document getBankPage() {
        CookieAndAuth cookieAndAuth = cookiesAndAuth();

        HashMap<String, String> cookies = cookieAndAuth.cookies;
        String authToken = cookieAndAuth.authToken;

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(SplashActivity.username, SplashActivity.usernameKey));
        formData.put("passwort", AES.decrypt(SplashActivity.password, SplashActivity.passwordKey));
        formData.put("loginhash", authToken);

        Document bankPage = scrapePage("https://ksw.nesa-sg.ch/index.php?pageid=21411", cookies, formData);
        return bankPage;
    }

    public static Document scrapePage(String url, HashMap<String, String> cookies, HashMap<String, String> formData) {
        Future<Document> pageFuture = executorService.submit(new PageScraper(cookies, formData, url));
        try {
            Document page = pageFuture.get();
            return page;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CookieAndAuth cookiesAndAuth() {
        Future<CookieAndAuth> login = executorService.submit(new CookieAndAuthScraper());
        try {
            CookieAndAuth loginResponse = login.get();
            return loginResponse;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}

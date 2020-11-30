package com.example.nesa;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginHandler {
    private static Executor executorService = Executors.newFixedThreadPool(1);

    final String LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
    final String LOGIN_ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=21311";

    public int isLoginSuccessful(String username, String password) throws IOException {
        Connection.Response loginForm = Jsoup.connect(LOGIN_FORM_URL)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .method(Connection.Method.GET)
                .execute();
        Document loginPage = loginForm.parse();
        HashMap<String, String> cookies = new HashMap<>(loginForm.cookies());

        String authToken = loginPage.select("div.mdl-cell:nth-child(3) > input:nth-child(3)")
                .first()
                .attr("value");
        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", AES.decrypt(username, LoginActivity.usernameKey));
        formData.put("passwort", AES.decrypt(password, LoginActivity.passwordKey));
        formData.put("loginhash", authToken);

        Connection.Response homePage = Jsoup.connect(LOGIN_ACTION_URL)
                .cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .execute();

        Document doc = homePage.parse();

        if (doc.select("div.mdl-cell:nth-child(4)").get(0).text().contains("Wichtige Dokumente")) {
            return LoginActivity.LOGIN_SUCCESSFUL;
        } else {
            return LoginActivity.LOGIN_FAILED;
        }
    }
}

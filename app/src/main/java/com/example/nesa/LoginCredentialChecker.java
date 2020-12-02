package com.example.nesa;

import android.util.Log;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class LoginCredentialChecker implements Callable<Integer> {

    String username, password;
    final String LOGIN_FORM_URL = "https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=";
    final String LOGIN_ACTION_URL = "https://ksw.nesa-sg.ch/index.php?pageid=";

    public LoginCredentialChecker(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public Integer call() throws Exception {
        try{
            Connection.Response loginForm = Jsoup.connect(LOGIN_FORM_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .method(Connection.Method.GET)
                    .timeout(60*1000)
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
            Log.d("log", doc.select("div.mdl-cell:nth-child(4)").get(0).text());
            if (doc.select("div.mdl-cell:nth-child(4)").get(0).text().contains("Wichtige Dokumente")) {
                return LoginActivity.LOGIN_SUCCESSFUL;
            } else {
                return LoginActivity.LOGIN_FAILED;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", String.valueOf(e.getMessage()));
            return LoginActivity.LOGIN_ERROR;
        }
    }
}

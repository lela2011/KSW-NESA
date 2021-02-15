package ch.kanti.nesa.futures;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.LoginActivity;
import ch.kanti.nesa.SplashActivity;
import ch.kanti.nesa.tables.User;

public class LoginCredentialChecker implements Callable<Integer> {
    //variable definition
    String username, password;
    //initialize credential checker
    public LoginCredentialChecker(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public Integer call() {
        try{
            //connect to login page
            Connection.Response loginForm = Jsoup.connect(SplashActivity.LOGIN_FORM_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .method(Connection.Method.GET)
                    .timeout(30000)
                    .execute();
            Document loginPage = loginForm.parse();
            //read out cookies
            HashMap<String, String> cookies = new HashMap<>(loginForm.cookies());
            //get authToken
            String authToken = loginPage.select("div.mdl-cell:nth-child(4) > input:nth-child(3)") //div.mdl-cell:nth-child(3) > input:nth-child(3)
                    .first()
                    .attr("value");
            //add username and password to request
            HashMap<String, String> formData = new HashMap<>();
            formData.put("login", AES.decrypt(username, SplashActivity.usernameKey));
            formData.put("passwort", AES.decrypt(password, SplashActivity.passwordKey));
            formData.put("loginhash", authToken);
            //connect to main page
            Connection.Response homePage = Jsoup.connect(SplashActivity.ACTION_URL)
                    .cookies(cookies)
                    .data(formData)
                    .method(Connection.Method.POST)
                    .execute();
            //check if credentials correct
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

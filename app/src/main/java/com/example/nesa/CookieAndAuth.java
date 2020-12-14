package com.example.nesa;

import org.jsoup.Connection;

import java.util.HashMap;

public class CookieAndAuth {

    public HashMap<String, String> cookies;
    public String authToken;

    public CookieAndAuth(HashMap<String, String> cookies, String authToken) {
        this.cookies = cookies;
        this.authToken = authToken;
    }
}
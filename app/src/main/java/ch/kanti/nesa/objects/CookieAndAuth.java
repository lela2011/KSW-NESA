package ch.kanti.nesa.objects;

import java.util.HashMap;

public class CookieAndAuth {

    public final HashMap<String, String> cookies;
    public final String authToken;

    public CookieAndAuth(HashMap<String, String> cookies, String authToken) {
        this.cookies = cookies;
        this.authToken = authToken;
    }
}

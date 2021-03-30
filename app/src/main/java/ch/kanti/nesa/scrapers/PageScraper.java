package ch.kanti.nesa.scrapers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.concurrent.Callable;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.App;
import ch.kanti.nesa.background.LoginHandler;

public class PageScraper implements Callable<Document> {

    HashMap<String, String> cookies, formData;
    String url;

    public PageScraper(HashMap<String, String> cookies, HashMap<String, String> formData, String url){
        this.cookies = cookies;
        this.formData = formData;
        this.url = url;
    }

    @Override
    public Document call() throws Exception {
        return Jsoup.connect(url)
                .cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .execute()
                .parse();
    }
}


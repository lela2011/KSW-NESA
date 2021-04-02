package ch.kanti.nesa.scrapers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class PageScraper implements Callable<Document> {

    final HashMap<String, String> cookies;
    final HashMap<String, String> formData;
    final String url;

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


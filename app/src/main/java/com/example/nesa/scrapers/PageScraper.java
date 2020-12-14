package com.example.nesa.scrapers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.nesa.SplashActivity;
import com.example.nesa.tables.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

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


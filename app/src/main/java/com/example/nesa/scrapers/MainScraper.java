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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.nesa.Repository.formData;

public class MainScraper implements Callable<String> {

    HashMap<String, String> cookies, formData;

    public MainScraper(HashMap<String, String> cookies, HashMap<String, String> formData){
        this.cookies = cookies;
        this.formData = formData;
    }

    @Override
    public String call() throws Exception {
        Connection.Response mainResponse = Jsoup.connect(SplashActivity.ACTION_URL)
                .cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .execute();

        Document mainPage = mainResponse.parse();
        Element start = mainPage.select("tr.mdl-table--row-dense:nth-child(4) > td:nth-child(2)").get(0);
        String text = start.text();

        return text;
    }
}


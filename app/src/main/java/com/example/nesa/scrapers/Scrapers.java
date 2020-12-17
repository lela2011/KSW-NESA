package com.example.nesa.scrapers;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.example.nesa.MainActivity;
import com.example.nesa.tables.AccountInfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Scrapers {

    public static ArrayList<AccountInfo> scrapeMain(Document page){
        Elements table = page.select("div.mdl-cell:nth-child(5) > table:nth-child(2) > tbody:nth-child(1) > tr");
        ArrayList<AccountInfo> userInfo = new ArrayList<>(8);
        for (int i = 1; i < 9; i++) {
            Element entry = table.get(i - 1);
            String text = entry.child(1).text();
            if(text.equals("")){
                text = "-";
            }
            userInfo.add(new AccountInfo(text, i));
        }
        return userInfo;
    }

    public static ArrayList<String> scrapeLinks(Document page) {
        ArrayList<String> links = new ArrayList<>();
        links.add(0, page.select("#menu1").first().attr("href"));
        links.add(1, page.select("#menu21311").first().attr("href"));
        links.add(2, page.select("#menu21111").first().attr("href"));
        links.add(3, page.select("#menu21411").first().attr("href"));
        return links;
    }

    public static void scrapeMarks(Document page) {

    }

    public static void scrapeAbsences(Document page){

    }

    public static void scrapeAccount(Document page){
        String balance = page.select("a.mdl-button").get(0).text();
        Log.d("balance", balance);
    }
}

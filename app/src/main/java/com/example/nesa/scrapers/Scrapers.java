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
            userInfo.add(new AccountInfo(text, i));
        }
        return userInfo;
    }

    public static void scrapeMarks(Document page) {

    }

    public static void scrapeAbsences(Document page){

    }

    public static void scrapeAccount(Document page){

    }
}

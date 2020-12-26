package com.example.nesa.scrapers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ContentScrapers {

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

    public static ArrayList<BankStatement> scrapeAccount(Document page){

        ArrayList<BankStatement> statements = new ArrayList<>();

        Elements table = page.select("#content-card > table:nth-child(6) > tbody:nth-child(1) > tr");
        table.remove(0);
        table.remove(table.last());
        for(int i = 0; i<table.size(); i++ ){
            Element statement = table.get(i);
            String date = statement.child(0).text();
            String name = statement.child(1).text();
            float amount = Float.parseFloat(statement.child(2).text().replace(" sFr", ""));
            float balance = Float.parseFloat(statement.child(3).text().replace(" sFr", ""));
            statements.add(new BankStatement(i, date, name, amount, balance));
        }
        return statements;
    }
}

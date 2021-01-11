package com.example.nesa.scrapers;

import android.util.Log;

import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

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

    public static void scrapeMarks(Document page) {
        ArrayList<Element> overview = new ArrayList<>();
        ArrayList<Element> detailView = new ArrayList<>();

        Elements table = page.select(".mdl-data-table > tbody:nth-child(1) > tr");
        table.remove(0);
        int i = 0;
        int l = 2;
        while (i < table.size()) {
            if (l == 2) {
                overview.add(table.get(i));
                l = 1;
            }
            else if (l == 1) {
                detailView.add(table.get(i));
                l = 2;
            }
            i+=l;
        }

        for (int g = 0; g < overview.size(); g++) {
            String subjectId = overview.get(g).select("td:nth-child(1) > b").get(0).text();
            String subjectName = overview.get(g).select("td:nth-child(1) > b").get(1).text();
            Float gradeAverage = Float.parseFloat(overview.get(g).select("td").get(1).text());

            for (int d = 0; d < detailView.size(); d++) {
                //String date = detailView.get(d).select("");
            }
        }
        Log.d("Hello", "waasssuuup");
    }

    public static void scrapeAbsences(Document page){

    }

    public static ArrayList<BankStatement> scrapeBank(Document page){

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
            String pk = i + "_" + date + "_" + name + "_" + amount + "_" + balance;
            statements.add(new BankStatement(pk ,i, date, name, amount, balance));
        }
        return statements;
    }

    public static ArrayList<AccountInfo> scrapeEmail(Document page) {
        ArrayList<AccountInfo> emails = new ArrayList<>();
        String schoolMail = page.select("#f0").get(0).attr("value");
        String privateMail = page.select("#f1").get(0).attr("value");
        emails.add(new AccountInfo(schoolMail, 9));
        emails.add(new AccountInfo(privateMail, 10));
        return emails;
    }
}

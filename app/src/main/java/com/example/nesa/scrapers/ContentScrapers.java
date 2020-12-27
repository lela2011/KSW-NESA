package com.example.nesa.scrapers;

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
            String pk = i + "_" + date + "_" + name + "_" + amount + "_" + balance;
            statements.add(new BankStatement(pk ,i, date, name, amount, balance));
        }
        return statements;
    }
}

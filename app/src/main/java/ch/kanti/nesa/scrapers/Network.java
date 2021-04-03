package ch.kanti.nesa.scrapers;

import android.net.Uri;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.App;
import ch.kanti.nesa.objects.LoginAndScrape;
import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;

public class Network {

    public static LoginAndScrape checkLoginAndPages (boolean checkAndScrape, boolean scrapePersonal, String username, String password) {
        boolean loginCorrect = false;

        ArrayList<AccountInfo> accountInfos = null;
        ArrayList<Absence> absences;
        ArrayList<BankStatement> bankStatements;
        SubjectsAndGrades subjectsAndGrades;

        String id = "";
        String transid = "";

        try {
            Connection.Response loginResponse = Jsoup.connect("https://ksw.nesa-sg.ch/loginto.php?mode=0&lang=")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .method(Connection.Method.GET)
                    .execute();

            HashMap<String, String> loginCookies = new HashMap<>(loginResponse.cookies());
            String authToken = loginResponse.parse().select("div.mdl-cell:nth-child(3) > input:nth-child(3)").first().attr("value");

            HashMap<String, String> formData = new HashMap<>();
            formData.put("login", AES.decrypt(username, App.usernameKey));
            formData.put("passwort", AES.decrypt(password, App.passwordKey));
            formData.put("loginhash", authToken);

            Connection.Response mainResponse = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=")
                    .cookies(loginCookies)
                    .data(formData)
                    .method(Connection.Method.POST)
                    .execute();

            Document mainPage = mainResponse.parse();

            HashMap<String, String> mainCookies = new HashMap<>(mainResponse.cookies());

            Elements checkOnline = mainPage.select("div.mdl-cell:nth-child(1) > p");

            if(checkOnline.size() != 0) {
                if (checkOnline.get(0).text().contains("Willkommen im Nesa.")) {
                    loginCorrect = true;
                }
            }

            if (loginCorrect) {
                Uri mainURI = Uri.parse("https://ksw.nesa-sg.ch/" + mainPage.select("#menu1").get(0).attr("href"));
                id = mainURI.getQueryParameter("id");
                transid = mainURI.getQueryParameter("transid");
            }

            if (checkAndScrape && loginCorrect) {

                Document gradesPage = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=21311&id=" + id + "&transid=" + transid)
                        .cookies(mainCookies)
                        .method(Connection.Method.POST)
                        .execute()
                        .parse();

                Document absencePage = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=21111&id=" + id + "&transid=" + transid)
                        .cookies(mainCookies)
                        .method(Connection.Method.POST)
                        .execute()
                        .parse();

                Document bankPage = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=21411&id=" + id + "&transid=" + transid)
                        .cookies(mainCookies)
                        .method(Connection.Method.POST)
                        .execute()
                        .parse();

                if (scrapePersonal) {
                    Document emailPage = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22500&id=" + id + "&transid=" + transid)
                            .cookies(mainCookies)
                            .method(Connection.Method.POST)
                            .execute()
                            .parse();

                    accountInfos = ContentScrapers.scrapeMain(mainPage, emailPage);
                }

                subjectsAndGrades = ContentScrapers.scrapeMarks(gradesPage);
                absences = ContentScrapers.scrapeAbsences(absencePage);
                bankStatements = ContentScrapers.scrapeBank(bankPage);

                logout(id, transid, mainCookies);

                return new LoginAndScrape(true, true, accountInfos, absences, bankStatements, subjectsAndGrades);
            } else {
                if (loginCorrect) {
                    logout(id, transid, mainCookies);
                }
                return new LoginAndScrape(loginCorrect, true, null, null, null, null);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new LoginAndScrape(false, false, null, null, null, null);
        }
    }

    private static void logout(String id, String transid, HashMap<String, String> mainCookies) {
        try {
            Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=9999&id=" + id + "&transid=" + transid)
                    .cookies(mainCookies)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

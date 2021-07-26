package ch.kanti.nesa.scrapers;

import android.net.Uri;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.App;
import ch.kanti.nesa.objects.LoginAndScrape;
import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Lesson;
import ch.kanti.nesa.tables.Student;

public class Network {

    public static LoginAndScrape checkLoginAndPages (boolean checkAndScrape, boolean scrapePersonal, boolean scrapeTimetable, boolean all, String username, String password) {
        boolean loginCorrect = false;

        ArrayList<Lesson> lessons = null;
        ArrayList<Lesson> examLessons = null;
        ArrayList<AccountInfo> accountInfos = null;
        ArrayList<Student> students = null;
        ArrayList<Absence> absences = null;
        ArrayList<BankStatement> bankStatements = null;
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

                    Document studentPage = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22348&id=" + id + "&transid=" + transid)
                            .cookies(mainCookies)
                            .method(Connection.Method.POST)
                            .execute()
                            .parse();

                    int courses = studentPage.select("label.mdl-radio").size();

                    List<Document> studentPages = new ArrayList<>();
                    for (int i = 0; i < courses; i++) {
                        Document page = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22348&listindex_s=" + i + "&id=" + id + "&transid=" + transid)
                                .cookies(mainCookies)
                                .method(Connection.Method.POST)
                                .execute()
                                .parse();

                        studentPages.add(page);
                    }


                    accountInfos = ContentScrapers.scrapeMain(mainPage, emailPage);
                    students = ContentScrapers.scrapeStudents(studentPages);
                }

                if (scrapeTimetable) {

                    lessons = new ArrayList<>();
                    examLessons = new ArrayList<>();

                    Document agendaPage = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22202&id=" + id + "&transid=" + transid)
                            .cookies(mainCookies)
                            .method(Connection.Method.POST)
                            .execute()
                            .parse();

                    String timetable = agendaPage.select("a.mdl-button:nth-child(2)").get(0).attr("style");
                    String events = agendaPage.select("a.mdl-button:nth-child(3)").get(0).attr("style");
                    String exams = agendaPage.select("a.mdl-button:nth-child(4)").get(0).attr("style");

                    if (!timetable.contains("background-color")) {
                        Connection.Response timetableResponse = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22202&id=" + id + "&transid=" + transid + "&eventtype=0_stp")
                                .cookies(mainCookies)
                                .method(Connection.Method.POST)
                                .execute();
                    }

                    if (events.contains("background-color")) {
                        Connection.Response eventsResponse = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22202&id=" + id + "&transid=" + transid + "&eventtype=5_trm")
                                .cookies(mainCookies)
                                .method(Connection.Method.POST)
                                .execute();
                    }

                    if (exams.contains("background-color")) {
                        Connection.Response examsResponse = Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=22202&id=" + id + "&transid=" + transid + "&eventtype=1_pru")
                                .cookies(mainCookies)
                                .method(Connection.Method.POST)
                                .execute();
                    }

                    LocalDate today = LocalDate.now();
                    LocalDate previousMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate lastDay = LocalDate.parse("2022-02-06");

                    if(all) {

                        while (!previousMonday.isEqual(lastDay)) {
                            Document timetableDocument = Jsoup.connect("https://ksw.nesa-sg.ch/scheduler_processor.php?view=week&curr_date=" + today.toString() + "&min_date=" + previousMonday.toString() + "&max_date=" + previousMonday.plusDays(7).toString() + "&ansicht=schueleransicht&id=" + id + "&transid=" + transid + "&pageid=22202&timeshift=-120")
                                    .cookies(mainCookies)
                                    .method(Connection.Method.GET)
                                    .execute()
                                    .parse();

                            ArrayList<Lesson> weekLessons = ContentScrapers.scrapeTimetable(timetableDocument);
                            lessons.addAll(weekLessons);

                            previousMonday = previousMonday.plusDays(7);
                        }

                        previousMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                        Jsoup.connect("https://ksw.nesa-sg.ch/index.php?pageid=21312&id=" + id + "&transid=" + transid)
                                .cookies(mainCookies)
                                .method(Connection.Method.POST)
                                .execute();

                        while (!previousMonday.isEqual(lastDay)) {

                            Document examDocument = Jsoup.connect("https://ksw.nesa-sg.ch/scheduler_processor.php?view=week&curr_date=" + today.toString() + "&min_date=" + previousMonday.toString() + "&max_date=" + previousMonday.plusDays(7).toString() + "&ansicht=klassenuebersicht&showOnlyThisClass=-2&id=" + id + "&transid=" + transid + "&pageid=21312&timeshift=-120")
                                    .cookies(mainCookies)
                                    .method(Connection.Method.GET)
                                    .execute()
                                    .parse();

                            ArrayList<Lesson> weekExams = ContentScrapers.scrapeExams(examDocument);
                            examLessons.addAll(weekExams);

                            previousMonday = previousMonday.plusDays(7);
                        }
                    } else {
                        Document timetableDocument = Jsoup.connect("https://ksw.nesa-sg.ch/scheduler_processor.php?view=week&curr_date=" + today.toString() + "&min_date=" + previousMonday.toString() + "&max_date=" + previousMonday.plusDays(7).toString() + "&ansicht=schueleransicht&id=" + id + "&transid=" + transid + "&pageid=22202&timeshift=-120")
                                .cookies(mainCookies)
                                .method(Connection.Method.GET)
                                .execute()
                                .parse();

                        lessons = ContentScrapers.scrapeTimetable(timetableDocument);

                        Document examDocument = Jsoup.connect("https://ksw.nesa-sg.ch/scheduler_processor.php?view=week&curr_date=" + today.toString() + "&min_date=" + previousMonday.toString() + "&max_date=" + previousMonday.plusDays(7).toString() + "&ansicht=klassenuebersicht&showOnlyThisClass=-2&id=" + id + "&transid=" + transid + "&pageid=21312&timeshift=-120")
                                .cookies(mainCookies)
                                .method(Connection.Method.GET)
                                .execute()
                                .parse();

                        examLessons = ContentScrapers.scrapeExams(examDocument);
                    }
                }

                subjectsAndGrades = ContentScrapers.scrapeMarks(gradesPage);
                absences = ContentScrapers.scrapeAbsences(absencePage);
                bankStatements = ContentScrapers.scrapeBank(bankPage);

                logout(id, transid, mainCookies);

                return new LoginAndScrape(true, true,  accountInfos, absences, bankStatements, students, lessons, examLessons, subjectsAndGrades);
            } else {
                if (loginCorrect) {
                    logout(id, transid, mainCookies);
                }
                return new LoginAndScrape(loginCorrect, true,  null, null, null, null,null, null, null);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new LoginAndScrape(false, false, null, null, null, null,null, null, null);
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

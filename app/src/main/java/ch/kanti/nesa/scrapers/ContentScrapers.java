package ch.kanti.nesa.scrapers;

import android.util.Log;

import androidx.work.Constraints;
import androidx.work.NetworkType;

import ch.kanti.nesa.SplashActivity;
import ch.kanti.nesa.SubjectsAndGrades;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static SubjectsAndGrades scrapeMarks(Document page) {
        ArrayList<String> gymProm = new ArrayList<>();
        gymProm.addAll(Arrays.asList("B", "C", "D", "F", "(?!EW)E", "(?!MU)M", "P", "^(?!BG)G$", "^G{2}$", "BG", "s.+", ".+[(]EF[)]", ".+[(]SP[)]", "EW", "PHI", "REL", "MU"));

        ArrayList<String> fms12Prom = new ArrayList<>();
        fms12Prom.addAll(Arrays.asList("D", "F", "(?!EW)E", "(?!MU)M", "B", "C", "P", "^(?!GE)G$", "^G{2}$", "GE", "s.+", "REL", "MU", "ÖK", "W", "SPO", "WLR", "ICT-A", "IB", "(?!PY)PE", "(?!PE)PY", "RH", ".+[(]SP[)]"));

        ArrayList<String> fms3Prom = new ArrayList<>();
        fms3Prom.addAll(Arrays.asList("D", "F", "(?!EW)E", "M", "B", "C", "P", "(?!GG)G", "GG", "GE", "s.+", "REL", "ÖK", "W", "WLR", "PE", "PY", ".+[(]SP[)]", "SPO", "RH", "MU", "IB", "ICT-A"));

        ArrayList<Element> overview = new ArrayList<>();
        ArrayList<Element> detailView = new ArrayList<>();
        ArrayList<Subjects> subjects = new ArrayList<>();
        ArrayList<Grades> gradesList = new ArrayList<>();
        Float grade = 0f;
        Float gradeAverage = 0f;

        String year = page.select("#uebersicht_bloecke > page:nth-child(1) > h3:nth-child(1)").get(0).text();
        ArrayList<String> yearChars = new ArrayList<>();
        yearChars.addAll(Arrays.asList(year.split("")));
        int yearFinal = Integer.parseInt(yearChars.get(yearChars.size() - 3));

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
            int counts = 0;
            String subjectId = overview.get(g).select("td:nth-child(1) > b").get(0).text();
            String[] checkSubIds = subjectId.split("-");
            String department = SplashActivity.sharedPreferences.getString("department", "Gymnasium");
            if (department.equals("Gymnasium")) {
                for (String expr : gymProm) {
                    Pattern p = Pattern.compile(expr);
                    Matcher m = p.matcher(checkSubIds[0]);
                    if (m.matches()) {
                        counts = 1;
                        break;
                    }
                }
            } else {
                if (yearFinal < 3) {
                    for (String expr : fms12Prom) {
                        Pattern p = Pattern.compile(expr);
                        Matcher m = p.matcher(subjectId);
                        if (m.find()) {
                            counts = 1;
                            break;
                        }
                    }
                } else {
                    for (String expr : fms3Prom) {
                        Pattern p = Pattern.compile(expr);
                        Matcher m = p.matcher(subjectId);
                        if (m.find()) {
                            counts = 1;
                            break;
                        }
                    }
                }
            }
            String subjectName = overview.get(g).select("td:nth-child(1)").get(0).text().replace(subjectId + " ", "");
            String gradeAverageString = overview.get(g).select("td").get(1).text().replace("*", "").replace("-", "");
            if(gradeAverageString.equals("")){
                gradeAverage = -1f;
            } else {
                gradeAverage = Float.parseFloat(gradeAverageString);
            }
            subjects.add(new Subjects(subjectName, "100", gradeAverage, calculatePluspoints(gradeAverage), subjectId, g, counts, counts));
            Elements grades = detailView.get(g).select("td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr");
            if (grades.size() != 0){
                grades.remove(0);
                grades.remove(grades.size()-1);
                for (int d = 0; d < grades.size(); d++) {
                    String date = grades.get(d).select("td").get(0).text();
                    String name = grades.get(d).select("td").get(1).text();
                    String detailString = grades.get(d).select("td > span").text();
                    String detailDiv = grades.get(d).select("td > div").text();
                    String gradeString = grades.get(d).select("td").get(2).text().replace(detailString, "").replace(detailDiv, "").replace(" ", "");
                    if (gradeString.equals("")) {
                        grade = -1f;
                    } else {
                        grade = Float.parseFloat(gradeString);
                    }
                    Float weight = Float.parseFloat(grades.get(d).select("td").get(3).text());
                    String gradeId = date + "_" + name;
                    gradesList.add(new Grades(gradeId, name, grade, weight, date, subjectId, d, g));
                }
            }
        }
        return new SubjectsAndGrades(subjects, gradesList);
    }

    public static ArrayList<Absence> scrapeAbsences(Document page){
        ArrayList<Absence> absencesList = new ArrayList<>();

        Elements absences = page.select("#uebersicht_bloecke > page:nth-child(1) > div:nth-child(2) > form:nth-child(3) > table:nth-child(2) > tbody:nth-child(2) > tr");
        absences.remove(0);
        for (int i = 0; i<2; i++) {
            absences.remove(absences.size()-1);
        }

        for (Element absenceElement : absences) {
            String date = absenceElement.select("td").get(0).text().replace(" ", "").replace("(*)", "");
            String time = absenceElement.select("td").get(1).text();
            String course = absenceElement.select("td").get(2).text();

            Absence absence = new Absence(date, time, course, 0);
            absencesList.add(absence);
        }

        try {
            Elements delays = page.select("table.mdl-data-table:nth-child(5) > tbody:nth-child(2) > tr");
            if (delays.size() != 0) {
                delays.remove(0);
                delays.remove(delays.size()-1);
                delays.remove(delays.size()-1);

                for (Element delay : delays) {
                    String date = delay.select("td").get(0).text().replace(" ", "").replace("(*)", "");
                    String time = delay.select("td").get(1).text();
                    String delayTime = delay.select("td").get(3).text() + " min";

                    Absence delayObj = new Absence(date, time, delayTime, 1);
                    absencesList.add(delayObj);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return absencesList;
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

    public static float calculatePluspoints(float gradeExact){
        DecimalFormat df = new DecimalFormat("#.###");
        float grade = Float.parseFloat(df.format(gradeExact));
        if (grade >= 5.75f) {
            return 2f;
        } else if (grade >= 5.25f) {
            return 1.5f;
        } else if (grade >= 4.75f) {
            return 1f;
        } else if (grade >= 4.24f) {
            return 0.5f;
        } else if (grade >= 3.75f) {
            return 0f;
        } else if(grade >= 3.25f) {
            return -1f;
        } else if (grade >= 2.75f) {
            return -2f;
        } else if (grade >= 2.25f) {
            return -3f;
        } else if (grade >= 1.75f) {
            return -4f;
        } else if (grade >= 1.25f) {
            return -5f;
        } else if (grade >= 1.0f) {
            return -6f;
        } else {
            return -10f;
        }
    }
}

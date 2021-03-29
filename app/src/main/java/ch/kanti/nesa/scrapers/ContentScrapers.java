package ch.kanti.nesa.scrapers;

import ch.kanti.nesa.objects.PromotionRule;
import ch.kanti.nesa.activities.SplashActivity;
import ch.kanti.nesa.objects.SubjectsAndGrades;
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
import java.util.List;
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
        ArrayList<String> gymProm = new ArrayList<>(Arrays.asList("B", "C", "D", "F", "(?!EW)E", "(?!MU)M", "P", "^(?!BG)G$", "^G{2}$", "BG", "s.+", ".+[(]EF[)]", ".+[(]SP[)]", "EW", "PHI", "REL", "MU"));

        ArrayList<String> fms12Prom = new ArrayList<>(Arrays.asList("D", "F", "(?!EW)E", "(?!MU)M", "B", "C", "P", "^(?!GE)G$", "^G{2}$", "GE", "s.+", "REL", "MU", "ÖK", "W", "SPO", "WLR", "ICT-A", "IB", "(?!PY)PE", "(?!PE)PY", "RH", ".+[(]SP[)]"));

        ArrayList<String> fms3Prom = new ArrayList<>(Arrays.asList("D", "F", "(?!EW)E", "M", "B", "C", "P", "^(?!GE)G$", "^G{2}$", "GE", "s.+", "REL", "ÖK", "W", "WLR", "(?!PY)PE", "(?!PE)PY", ".+[(]SP[)]", "SPO", "RH", "MU", "IB", "ICT-A"));

        ArrayList<Element> overview = new ArrayList<>();
        ArrayList<Element> detailView = new ArrayList<>();
        ArrayList<Subjects> subjects = new ArrayList<>();
        ArrayList<Grades> gradesList = new ArrayList<>();
        Float grade = 0f;
        Float gradeAverage = 0f;

        String year = page.select("#uebersicht_bloecke > page:nth-child(1) > h3:nth-child(1)").get(0).text();
        ArrayList<String> yearChars = new ArrayList<>(Arrays.asList(year.split("")));
        int yearFinal = Integer.parseInt(yearChars.get(yearChars.size() - 3));
        SplashActivity.editor.putInt("year", yearFinal).apply();
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
                    gradesList.add(new Grades(name, subjectId, date, grade, weight, d, g));
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
            int excused = 0;
            String course = absenceElement.select("td").get(2).text();

            Absence absence = new Absence(date, time, course, 0, excused);
            absencesList.add(absence);
        }

        Elements delays = page.select("table.mdl-data-table:nth-child(5) > tbody:nth-child(2) > tr");
        if (delays.size() != 0) {
            delays.remove(0);
            delays.remove(delays.size()-1);
            delays.remove(delays.size()-1);

            for (Element delay : delays) {
                String date = delay.select("td").get(0).text().replace(" ", "").replace("(*)", "");
                String time = delay.select("td").get(1).text();
                String delayTime = delay.select("td").get(3).text() + " min";
                String excused = delay.select("td").get(1).text();
                int excusedInt = 0;
                if(!excused.equals("Nein")) {
                    excusedInt = 1;
                }

                Absence delayObj = new Absence(date, time, delayTime, 1, excusedInt);
                absencesList.add(delayObj);
            }
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
        } else if (grade >= 4.25f) {
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

    public static void calculatePromotionPoints(List<Subjects> subjects) {
        ArrayList<PromotionRule> gymRules = new ArrayList<>();
        gymRules.add(new PromotionRule(2,"sMU",".+[(]SP[)]", 2f/3f, 1f/3f, false));
        gymRules.add(new PromotionRule(2,"sFB","sBW", 0.5f, 0.5f, false));
        gymRules.add(new PromotionRule(2,"sP","sM", 0.5f, 0.5f, true));
        gymRules.add(new PromotionRule(2,"sB","sC", 0.5f, 0.5f, true));
        gymRules.add(new PromotionRule(2,"MU","BG", 0.5f, 0.5f, true));

        ArrayList<PromotionRule> fms12Rules = new ArrayList<>();
        fms12Rules.add(new PromotionRule(2, "sM", "M", 0.5f, 0.5f, true));
        fms12Rules.add(new PromotionRule(2, "sPY", "PY", 0.5f, 0.5f, true));
        fms12Rules.add(new PromotionRule(2, "MU", ".+[(]SP[)]", 2f/3f, 1f/3f, false));
        fms12Rules.add(new PromotionRule(2, "sPBF", "sBFU", 0.5f, 0.5f, true));
        fms12Rules.add(new PromotionRule(3, "sSWT", "sD", "D", 0.125f, 0.375f, 0.5f, true));
        fms12Rules.add(new PromotionRule(3, "sSWT", "sZWT", "sPFB", 0.25f, 0.25f, 0.5f, true));
        fms12Rules.add(new PromotionRule(2, "sZWT", "sDKF", 0.25f, 0.75f, true));
        fms12Rules.add(new PromotionRule(3, "sKA", "sSWT", "D", 0.375f, 0.125f, 0.5f, true));
        fms12Rules.add(new PromotionRule(2, "sKoiK", "E", 1f/3f, 2f/3f, true));

        ArrayList<PromotionRule> fms3Rules = new ArrayList<>();
        fms3Rules.add(new PromotionRule(2, "sM", "M", 0.5f, 0.5f, true));
        fms3Rules.add(new PromotionRule(2, "sPY", "PY", 0.5f, 0.5f, true));
        fms3Rules.add(new PromotionRule(2, "SPO", "RH", 2f/3f, 1f/3f, false));
        fms3Rules.add(new PromotionRule(2, "sMU", ".+[(]SP[)]", 2f/3f, 1f/3f, false));
        fms3Rules.add(new PromotionRule(2, "sPBF", "sBFU", 0.5f, 0.5f, true));
        fms3Rules.add(new PromotionRule(3, "sSWT", "sD", "D", 0.125f, 0.375f, 0.5f, true));
        fms3Rules.add(new PromotionRule(3, "sSWT", "sZWT", "sPFB", 0.25f, 0.25f, 0.5f, true));
        fms3Rules.add(new PromotionRule(2, "sZWT", "sDKF", 0.25f, 0.75f, true));
        fms3Rules.add(new PromotionRule(3, "sKA", "sSWT", "D", 0.375f, 0.125f, 0.5f, true));
        fms3Rules.add(new PromotionRule(2, "sKoiK", "E", 1f/3f, 2f/3f, true));

        ArrayList<Float> pluspoints = new ArrayList<>();
        ArrayList<Float> modifiedGrades = new ArrayList<>();

        int finalYear = SplashActivity.sharedPreferences.getInt("year", -1);
        String department = SplashActivity.sharedPreferences.getString("department", "Gymnasium");

        subjects.add(new Subjects("Physik", "100", 6.0f, 2.0f, "sP-2P-HB", 16, 1, 1));

        if(department.equals("Gymnasium")) {
            for(int i = 0; i < gymRules.size(); i++) {
                PromotionRule rule = gymRules.get(i);
                Pattern pat1 = Pattern.compile(rule.getId1());
                Pattern pat2 = Pattern.compile(rule.getId2());
                Pattern pat3 = null;
                if(rule.getId3() != null) {
                    pat3 = Pattern.compile(rule.getId3());
                }

                ArrayList<Float> grades = new ArrayList<>();
                ArrayList<Float> weight = new ArrayList<>();

                for(int k = 0; k < subjects.size(); k++) {
                    Subjects subject = subjects.get(k);
                    String id = subject.getId();
                    if(pat1.matcher(id).find()) {
                        grades.add(subject.getGradeAverage());
                        weight.add(rule.getWeight1());
                    } else if (pat2.matcher(id).find()) {
                        grades.add(subject.getGradeAverage());
                        weight.add(rule.getWeight2());
                    } else if (pat3 != null) {
                        if (pat3.matcher(id).find())
                        grades.add(subject.getGradeAverage());
                        weight.add(rule.getWeight3());
                    }
                }
                if(grades.size() == rule.getSubjectsCount()) {
                    float average = 0;
                    if(rule.isRound()) {
                        for (int l = 0; l < grades.size(); i++) {
                            grades.set(l, Math.round(grades.get(l)*2)/2.0f);
                        }
                    }
                    for (int l = 0; l < grades.size(); i++) {
                        average = average + grades.get(l) * weight.get(l);
                    }
                    pluspoints.add(calculatePluspoints(average));
                }
            }
        }
    }
}

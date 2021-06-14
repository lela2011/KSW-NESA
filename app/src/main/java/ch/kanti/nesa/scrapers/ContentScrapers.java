package ch.kanti.nesa.scrapers;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import ch.kanti.nesa.App;
import ch.kanti.nesa.objects.PromotionRule;
import ch.kanti.nesa.objects.SubjectsAndGrades;
import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Lesson;
import ch.kanti.nesa.tables.Student;
import ch.kanti.nesa.tables.Subject;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContentScrapers {

    static final HashMap<String, Integer> parallelLessons = new HashMap<>();

    public static ArrayList<AccountInfo> scrapeMain(Document mainPage, Document emailPage){
        Elements tableLoggedOut = mainPage.select("div.mdl-cell:nth-child(4) > table:nth-child(2) > tbody:nth-child(1) > tr");
        Elements tableNotLoggedOut = mainPage.select("div.mdl-cell:nth-child(5) > table:nth-child(2) > tbody:nth-child(1) > tr");
        String schoolMail = emailPage.select("#f0").get(0).attr("value");
        String privateMail = emailPage.select("#f1").get(0).attr("value");
        Elements table;

        if(tableLoggedOut.size() > tableNotLoggedOut.size()) {
            table = tableLoggedOut;
        } else {
            table = tableNotLoggedOut;
        }

        ArrayList<AccountInfo> userInfo = new ArrayList<>(10);
        for (int i = 0; i < table.size(); i++) {
            Element entry = table.get(i);
            String text = entry.child(1).text();
            if(text.equals("")){
                text = "-";
            }
            userInfo.add(new AccountInfo(text, i));
        }

        userInfo.add(new AccountInfo(schoolMail, userInfo.size()));
        userInfo.add(new AccountInfo(privateMail, userInfo.size()+1));
        return userInfo;
    }

    @SuppressLint("ApplySharedPref")
    public static SubjectsAndGrades scrapeMarks(Document page) {
        ArrayList<String> gymProm = new ArrayList<>(Arrays.asList("B", "C", "D", "F", "(?!EW)E", "(?!MU)M", "P", "^(?!BG)G$", "^G{2}$", "BG", "s.+", ".+[(]EF[)]", ".+[(]SP[)]", "EW", "PHI", "REL", "MU"));

        ArrayList<String> fms12Prom = new ArrayList<>(Arrays.asList("D", "F", "(?!EW)E", "(?!MU)M", "B", "C", "P", "^(?!GE)G$", "^G{2}$", "GE", "s.+", "REL", "MU", "ÖK", "W", "SPO", "WLR", "ICT-A", "IB", "(?!PY)PE", "(?!PE)PY", "RH", ".+[(]SP[)]"));

        ArrayList<String> fms3Prom = new ArrayList<>(Arrays.asList("D", "F", "(?!EW)E", "M", "B", "C", "P", "^(?!GE)G$", "^G{2}$", "GE", "s.+", "REL", "ÖK", "W", "WLR", "(?!PY)PE", "(?!PE)PY", ".+[(]SP[)]", "SPO", "RH", "MU", "IB", "ICT-A"));

        ArrayList<Element> overview = new ArrayList<>();
        ArrayList<Element> detailView = new ArrayList<>();
        ArrayList<Subject> subjects = new ArrayList<>();
        ArrayList<Grade> gradeList = new ArrayList<>();
        float grade;
        float gradeAverage;

        String year = page.select("#uebersicht_bloecke > page:nth-child(1) > h3:nth-child(1)").get(0).text();
        year = year.replaceAll("[^0-9]+","");
        int yearFinal = Integer.parseInt(year);
        App.sharedPreferences.edit().putInt("year", yearFinal).apply();
        Elements table = page.select(".mdl-data-table > tbody:nth-child(1) > tr");
        table.remove(0);
        int i = 0;
        int l = 2;

        for (int h = 0; h < table.size(); h++) {
            String tableClass = table.get(h).attr("class").trim();
            String tableId = table.get(h).attr("id").trim();
            if(tableClass.contains("detailrow")) {
                detailView.add(table.get(h));
            } else if (!tableId.contains("schueleruebersicht")) {
                if (table.get(h).childrenSize() == 5) {
                    overview.add(table.get(h));
                } else if (table.get(h).childrenSize() == 1){
                    detailView.add(table.get(h));
                }
            }
        }

        for (int g = 0; g < overview.size(); g++) {
            int counts = 0;
            String subjectId = overview.get(g).select("td:nth-child(1) > b").get(0).text();
            String[] checkSubIds = subjectId.split("-");
            String department = App.sharedPreferences.getString("department", "Gymnasium");
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
            subjects.add(new Subject(subjectName, "100", gradeAverage, calculatePluspoints(gradeAverage), subjectId, g, counts, counts));
            Elements grades = detailView.get(g).select("td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr");
            if (grades.size() != 0) {
                if(g+1 <= detailView.size()-1) {
                    if(grades.last().text().contains("Aktueller Durchschnitt")) {
                        grades.remove(grades.size()-1);
                    } else {
                        Elements additionalGrades = detailView.get(g+1).select("td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr");
                        additionalGrades.remove(additionalGrades.size()-1);
                        grades.addAll(additionalGrades);
                        detailView.remove(g+1);
                    }
                } else if (g == detailView.size()-1) {
                    if(grades.last().text().contains("Aktueller Durchschnitt")) {
                        grades.remove(grades.size()-1);
                    }
                }
                grades.remove(0);
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
                    float weight = Float.parseFloat(grades.get(d).select("td").get(3).text());
                    gradeList.add(new Grade(name, subjectId, date, grade, weight, d, g));
                }
            }
        }
        return new SubjectsAndGrades(subjects, gradeList);
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

        Elements table = page.select("#content-card > table:nth-child(8) > tbody > tr");
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

    public static ArrayList<Lesson> scrapeTimetable (Document page) {
        //<editor-fold desc="rooms">
        HashMap<Integer, String> rooms = new HashMap<>();
        rooms.put(0,"Ausserhalb");
        rooms.put(1,"01");
        rooms.put(2,"02");
        rooms.put(3,"03");
        rooms.put(4,"04");
        rooms.put(5,"05");
        rooms.put(6,"06");
        rooms.put(7,"07");
        rooms.put(8,"08");
        rooms.put(9,"09");
        rooms.put(10,"10");
        rooms.put(11,"11");
        rooms.put(12,"12");
        rooms.put(13,"13");
        rooms.put(14,"14");
        rooms.put(15,"15");
        rooms.put(16,"16");
        rooms.put(17,"Aula");
        rooms.put(18,"B");
        rooms.put(19,"BL");
        rooms.put(20,"BLapW");
        rooms.put(21,"Ch");
        rooms.put(22,"CL");
        rooms.put(23,"E01");
        rooms.put(24,"E02");
        rooms.put(25,"E03");
        rooms.put(26,"E04");
        rooms.put(27,"E05");
        rooms.put(28,"E06");
        rooms.put(29,"E07");
        rooms.put(30,"E08");
        rooms.put(31,"E11");
        rooms.put(32,"E12");
        rooms.put(33,"E13");
        rooms.put(34,"E16");
        rooms.put(35,"E17");
        rooms.put(36,"E20");
        rooms.put(37,"E22");
        rooms.put(38,"E24");
        rooms.put(39,"E25");
        rooms.put(40,"E26");
        rooms.put(41,"E30");
        rooms.put(42,"E31");
        rooms.put(43,"E32");
        rooms.put(44,"E33");
        rooms.put(45,"E34");
        rooms.put(46,"E35");
        rooms.put(47,"E36");
        rooms.put(48,"Gg");
        rooms.put(49,"GgLapW");
        rooms.put(50,"H");
        rooms.put(51,"Hb");
        rooms.put(52,"Hf1");
        rooms.put(53,"ILapW");
        rooms.put(54,"Inf");
        rooms.put(55,"Inf2");
        rooms.put(56,"K");
        rooms.put(57,"Kr");
        rooms.put(58,"LBiblio");
        rooms.put(59,"Lehrerbüro");
        rooms.put(60,"LZ1");
        rooms.put(61,"LZ2");
        rooms.put(62,"LZ3");
        rooms.put(63,"m1");
        rooms.put(64,"m10");
        rooms.put(65,"m2");
        rooms.put(66,"m3");
        rooms.put(67,"m4");
        rooms.put(68,"m5");
        rooms.put(69,"m6");
        rooms.put(70,"m7");
        rooms.put(71,"m8");
        rooms.put(72,"m9");
        rooms.put(73,"Mediothek");
        rooms.put(74,"Mensa");
        rooms.put(75,"Nat");
        rooms.put(76,"Ph");
        rooms.put(77,"PL1");
        rooms.put(78,"PL2");
        rooms.put(79,"PLapW");
        rooms.put(80,"R1");
        rooms.put(81,"R2");
        rooms.put(82,"RT");
        rooms.put(83,"SLI");
        rooms.put(84,"Z");
        //</editor-fold>
        //<editor-fold desc="times">
        /*HashMap<String, Integer> times = new HashMap<>();
        times.put("00:00",0);
        times.put("07:40",1);
        times.put("08:30",2);
        times.put("09:35",3);
        times.put("10:25",4);
        times.put("11:20",5);
        times.put("12:10",6);
        times.put("13:00",7);
        times.put("13:50",8);
        times.put("14:45",9);
        times.put("15:35",10);
        times.put("16:30",11);
        times.put("17:20",12);
        times.put("18:00",13);
        times.put("19:00",14);
        times.put("20:00",15);
        times.put("21:00",16);*/
        //</editor-fold>
        //<editor-fold desc="markings">
        HashMap<String, String> markings = new HashMap<>();
        markings.put("none","Keine Markierung");
        markings.put("new","Neu eingef\u00fcgt");
        markings.put("moved","Verschoben");
        markings.put("deleted","Gel\u00f6scht");
        markings.put("100","Auftrag via Teams/Mail");
        //</editor-fold>

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ArrayList<Lesson> lessonsList = new ArrayList<>();
        List<Lesson> dayLessons = new ArrayList<>();
        LocalDate lastDate = null;
        Elements lessons = page.getElementsByTag("event");
        for (Element temp : lessons) {
            try {
                String[] start_date = temp.getElementsByTag("start_date").get(0).data().replace("--","").replace("[CDATA[","").replace("]]","").split(" ");
                /*if (lastDate == null) {
                    lastDate = LocalDate.parse(start_date[0], formatter);
                    resetSublessons();
                }
                if (LocalDate.parse(start_date[0], formatter).isAfter(lastDate)) {
                    lastDate = LocalDate.parse(start_date[0], formatter);
                    for (Lesson daylesson : dayLessons) {
                        daylesson.setSiblingLessons(parallelLessons.get(daylesson.getStartTime()));
                    }
                    lessonsList.addAll(dayLessons);
                    dayLessons.clear();
                    resetSublessons();
                }*/
                String[] end_date = temp.getElementsByTag("end_date").get(0).data().replace("[CDATA[","").replace("]]","").split(" ");
                String subject = temp.getElementsByTag("fachkuerzel").get(0).data().replace("[CDATA[","").replace("]]","");
                String teacherShort = temp.getElementsByTag("lehrerkuerzel").get(0).data().replace("[CDATA[","").replace("]]","");
                String room = rooms.get(Integer.parseInt(temp.getElementsByTag("zimmer").get(0).data().replace("[CDATA[","").replace("]]","")));
                String marking = markings.get(temp.getElementsByTag("markierung").get(0).data().replace("[CDATA[","").replace("]]",""));
                String color = temp.getElementsByTag("color").get(0).data().replace("[CDATA[","").replace("]]","");
                WeekFields weekField = WeekFields.of(Locale.getDefault());
                int week = LocalDate.parse(start_date[0]).get(weekField.weekOfWeekBasedYear());
                Lesson lesson = new Lesson(start_date[0], week, start_date[1], end_date[1], subject, teacherShort, room, marking, null, color, false, App.getLessonIndex(LocalTime.parse(start_date[1])));
                lessonsList.add(lesson);
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        return lessonsList;
    }

    public static ArrayList<Lesson> scrapeExams(Document page) {
        ArrayList<Lesson> exams = new ArrayList<>();
        Elements lessons = page.getElementsByTag("event");
        for (Element temp : lessons) {
            try {
                String[] start_date = temp.getElementsByTag("start_date").get(0).data().replace("--","").replace("[CDATA[","").replace("]]","").split(" ");
                String[] end_date = temp.getElementsByTag("end_date").get(0).data().replace("[CDATA[","").replace("]]","").split(" ");
                String[] subjectTeacher = temp.getElementsByTag("text").get(0).data().replace("[CDATA[","").replace("]]","").split("-");
                String teacher = "-";
                try {
                    teacher = subjectTeacher[2];
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                String marking = temp.getElementsByTag("markierung").get(0).data().replace("[CDATA[","").replace("]]","");
                String color = temp.getElementsByTag("color").get(0).data().replace("[CDATA[","").replace("]]","");
                String comment = temp.getElementsByTag("kommentar").get(0).data().replace("[CDATA[","").replace("]]","");
                WeekFields weekField = WeekFields.of(Locale.getDefault());
                int week = LocalDate.parse(start_date[0]).get(weekField.weekOfWeekBasedYear());
                Lesson lesson = new Lesson(start_date[0], week, start_date[1], end_date[1], subjectTeacher[0], teacher, null, marking, comment, color, true, App.getLessonIndex(LocalTime.parse(start_date[1])));
                exams.add(lesson);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return exams;
    }

    private static void resetSublessons() {
        parallelLessons.put("07:40",0);
        parallelLessons.put("08:30",0);
        parallelLessons.put("09:35",0);
        parallelLessons.put("10:25",0);
        parallelLessons.put("11:20",0);
        parallelLessons.put("12:10",0);
        parallelLessons.put("13:00",0);
        parallelLessons.put("13:50",0);
        parallelLessons.put("14:45",0);
        parallelLessons.put("15:35",0);
        parallelLessons.put("16:30",0);
        parallelLessons.put("17:20",0);
        parallelLessons.put("18:00",0);
        parallelLessons.put("19:00",0);
        parallelLessons.put("20:00",0);
        parallelLessons.put("21:00",0);
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

    public static float calculatePromotionPoints(List<Subject> subjects) {

        ArrayList<Float> pluspoints = new ArrayList<>();

        ArrayList<PromotionRule> gymRules = new ArrayList<>();
        gymRules.add(new PromotionRule(2,"sMU",".+[(]SP[)]", "NULL", 2f/3f, 1f/3f, 0, false));
        gymRules.add(new PromotionRule(2,"sFB","sBW", "NULL", 0.5f, 0.5f, 0, false));
        gymRules.add(new PromotionRule(2,"sP","sM", "NULL", 0.5f, 0.5f, 0, true));
        gymRules.add(new PromotionRule(2,"sB","sC", "NULL", 0.5f, 0.5f, 0, true));
        gymRules.add(new PromotionRule(2,"MU","BG", "NULL", 0.5f, 0.5f, 0, true));

        ArrayList<PromotionRule> fms12Rules = new ArrayList<>();
        fms12Rules.add(new PromotionRule(2, "sM", "M", "NULL", 0.5f, 0.5f, 0,true));
        fms12Rules.add(new PromotionRule(2, "sPY", "PY", "NULL",0.5f, 0.5f,0, true));
        fms12Rules.add(new PromotionRule(2, "MU", ".+[(]SP[)]","NULL", 2f/3f, 1f/3f,0, false));
        fms12Rules.add(new PromotionRule(2, "sPBF", "sBFU","NULL", 0.5f, 0.5f,0, true));
        fms12Rules.add(new PromotionRule(3, "sSWT", "sD", "D", 0.125f, 0.375f, 0.5f, true));
        fms12Rules.add(new PromotionRule(3, "sSWT", "sZWT", "sPFB", 0.25f, 0.25f, 0.5f, true));
        fms12Rules.add(new PromotionRule(2, "sZWT", "sDKF", "NULL",0.25f, 0.75f, 0,true));
        fms12Rules.add(new PromotionRule(3, "sKA", "sSWT", "D", 0.375f, 0.125f, 0.5f, true));
        fms12Rules.add(new PromotionRule(2, "sKoiK", "E", "NULL",1f/3f, 2f/3f,0, true));

        ArrayList<PromotionRule> fms3Rules = new ArrayList<>();
        fms3Rules.add(new PromotionRule(2, "sM", "M","NULL", 0.5f, 0.5f,0, true));
        fms3Rules.add(new PromotionRule(2, "sPY", "PY","NULL", 0.5f, 0.5f, 0,true));
        fms3Rules.add(new PromotionRule(2, "SPO", "RH", "NULL",2f/3f, 1f/3f,0, false));
        fms3Rules.add(new PromotionRule(2, "sMU", ".+[(]SP[)]","NULL", 2f/3f, 1f/3f,0, false));
        fms3Rules.add(new PromotionRule(2, "sPBF", "sBFU", "NULL",0.5f, 0.5f,0, true));
        fms3Rules.add(new PromotionRule(3, "sSWT", "sD", "D", 0.125f, 0.375f, 0.5f, true));
        fms3Rules.add(new PromotionRule(3, "sSWT", "sZWT", "sPFB", 0.25f, 0.25f, 0.5f, true));
        fms3Rules.add(new PromotionRule(2, "sZWT", "sDKF", "NULL",0.25f, 0.75f, 0,true));
        fms3Rules.add(new PromotionRule(3, "sKA", "sSWT", "D", 0.375f, 0.125f, 0.5f, true));
        fms3Rules.add(new PromotionRule(2, "sKoiK", "E","NULL", 1f/3f, 2f/3f,0, true));

        //subjects.add(new Subject("Physik", "100", 5.0f, 1.5f, "sP-2P-HB", 16, 1, 1));

        int finalYear = App.sharedPreferences.getInt("year", -1);
        String department = App.sharedPreferences.getString("department", "Gymnasium");

        ArrayList<PromotionRule> rules = new ArrayList<>();

        if(department.equals("Gymnasium")) {
            rules.addAll(gymRules);
        } else {
            if (finalYear < 3) {
                rules.addAll(fms12Rules);
            } else {
                rules.addAll(fms3Rules);
            }
        }

        HashMap<String, Float> subjectsMap = new HashMap<>();
        for(Subject temp : subjects) {
            if (temp.getCountsAverage() == 1) {
                subjectsMap.put(temp.getId(), temp.getGradeAverage());
            }
        }

        for (PromotionRule rule : rules) {

            Pattern pat1 = Pattern.compile(rule.getId1());
            Pattern pat2 = Pattern.compile(rule.getId2());
            Pattern pat3 = Pattern.compile(rule.getId3());

            List<String> subs = subjectsMap.keySet()
                    .stream()
                    .filter(c -> pat1.matcher(c).find() | pat2.matcher(c).find() | pat3.matcher(c).find())
                    .collect(Collectors.toList());

            float average = 0;

            if (subs.size() == rule.getSubjectsCount()) {
                for (String foundSub : subs) {
                    float grade = subjectsMap.get(foundSub);
                    if (rule.isRound()) {
                        grade = Math.round(grade*2.0f)/2.0f;
                    }
                    if (pat1.matcher(foundSub).find()) {
                        average += grade * rule.getWeight1();
                        subjectsMap.remove(foundSub);
                    } else if (pat2.matcher(foundSub).find()) {
                        average += grade * rule.getWeight2();
                        subjectsMap.remove(foundSub);
                    } else if (pat3.matcher(foundSub).find()) {
                        average += grade * rule.getWeight3();
                        subjectsMap.remove(foundSub);
                    }
                }
                pluspoints.add(calculatePluspoints(average));
            }
        }
        ArrayList<Float> unmodified = new ArrayList<>(subjectsMap.values());
        for (float average : unmodified) {
            float calculatedPluspoint = calculatePluspoints(average);
            if (calculatedPluspoint != -10.0f) {
                pluspoints.add(calculatedPluspoint);
            }
        }

        float pluspointsSum = (float) pluspoints.stream()
                .mapToDouble(Float::floatValue)
                .sum();

        return pluspointsSum;
    }

    public static ArrayList<Student> scrapeStudents(List<Document> documents) {
        HashMap<String, Student> studentSet = new HashMap<>();
        for (Document page : documents) {
            Elements studentRows = page.select("#cls-table-Kursliste > tbody:nth-child(3) > tr");
            for (Element row : studentRows) {
                String name = row.select("td").get(1).text() + " " + row.select("td").get(2).text();
                if (!studentSet.containsKey(name)) {
                    String gender = row.select("td").get(3).text();
                    String degree = row.select("td").get(4).text();
                    String bilingual = row.select("td").get(5).text();
                    String course = row.select("td").get(6).text();
                    String address = row.select("td").get(7).text() + ", " + row.select("td").get(8).text() + " " + row.select("td").get(9).text();
                    String phone = row.select("td").get(10).text();
                    String additionalCourses = row.select("td").get(11).text();
                    String status = row.select("td").get(12).text();
                    Student student = new Student(name, gender, degree, bilingual, course, address, phone, additionalCourses, status);
                    studentSet.put(name, student);
                }
            }
        }
        ArrayList<Student> students = new ArrayList<>(studentSet.values());
        return students;
    }
}

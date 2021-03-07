package ch.kanti.nesa;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import ch.kanti.nesa.daos.GradesDAO;
import ch.kanti.nesa.tables.Grades;

public class GradesRepository {
    GradesDAO gradesDAO;
    Context context;

    public GradesRepository(Application application) {
        Database database = Database.getInstance(application);
        gradesDAO = database.gradesDAO();
        context = application.getApplicationContext();
    }

    public GradesRepository(Context context) {
        Database database = Database.getInstance(context);
        gradesDAO = database.gradesDAO();
        this.context = context;
    }

    public void insert(List<Grades> grades) {
        Database.databaseWriteExecutor.execute(() -> {
            int newGradesSize = 0;
            //grades.add(3, new Grades("Bio 4 - 2P","B-2P-ZI", "03.09.2020", 6.0f, 1.0f, 4,  0));
            //grades.remove(1);
            //grades.get(1).setGrade(6.0f);
            int oldGradesSize = gradesDAO.size();
            if (oldGradesSize != 0) { //oldGradesSize <= grades.size() &&
                List<Grades> oldGrades = gradesDAO.getAllGradesOrdered();
                List<Grades> newGrades = new ArrayList<>(grades);

                for (int i = 0; i < oldGrades.size(); i++) {
                    for (int k = 0; k < newGrades.size(); k++) {
                        k = newGradesSize;
                        if (oldGrades.get(i).compare(newGrades.get(k))) {
                            newGrades.remove(k);
                            oldGrades.remove(i);
                            i--;
                            break;
                        } else if (oldGrades.get(i+1).compare(newGrades.get(k))) {
                            newGrades.remove(k);
                            oldGrades.remove(i+1);
                            break;
                        }
                        newGradesSize++;
                    }
                }

                List<String> subjectsModified = new ArrayList<>();
                for (int i = 0; i < newGrades.size(); i++) {
                    if (!subjectsModified.contains(newGrades.get(i).getSubjectId())) {
                        subjectsModified.add(newGrades.get(i).getSubjectId());
                    }
                }

                for (int i = 0; i < oldGrades.size(); i++) {
                    if (!subjectsModified.contains(oldGrades.get(i).getSubjectId())) {
                        subjectsModified.add(oldGrades.get(i).getSubjectId());
                    }
                }

                for (int i = 0; i < subjectsModified.size(); i++) {
                    gradesDAO.deleteBySubject(subjectsModified.get(i));
                }

                List<Grades> gradesToAdd = new ArrayList<>();

                for (Grades grade : grades) {
                    if (subjectsModified.contains(grade.getSubjectId())) {
                        gradesToAdd.add(grade);
                    }
                }

                gradesDAO.insert(gradesToAdd);

                List<Grades> gradesModified = new ArrayList<>();

                gradesModified.addAll(newGrades);
                gradesModified.addAll(oldGrades);

                for (int i = 0; i < gradesModified.size(); i++) {
                    Grades grade = gradesModified.get(i);
                    Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(grade.getExam())
                            .setContentText("The grade \"" + grade.getExam() + "\" was modified")
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine("Subject: " + grade.getSubjectId())
                                    .addLine("Exam: " + grade.getExam())
                                    .addLine("Grade: " + grade.getGrade())
                                    .setBigContentTitle("The grade \"" + grade.getExam() + "\" was modified"))
                            .setSmallIcon(R.mipmap.icon_nesa)
                            .build();

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(i, notification);
                }

            } else if (grades.size() < oldGradesSize) {
                gradesDAO.deleteAll();
                gradesDAO.insert(grades);
            } else if (oldGradesSize == 0) {
                gradesDAO.insert(grades);
            }
        });
    }

    LiveData<List<Grades>> getBySubject(String passedSubject) {
        return gradesDAO.getBySubject(passedSubject);
    }
}

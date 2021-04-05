package ch.kanti.nesa;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import ch.kanti.nesa.activities.MainActivity;
import ch.kanti.nesa.daos.GradesDAO;
import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Grade;

public class GradesRepository {
    final GradesDAO gradesDAO;
    final SubjectsDAO subjectsDao;
    final Context context;

    public GradesRepository(Application application) {
        Database database = Database.getInstance(application);
        gradesDAO = database.gradesDAO();
        subjectsDao = database.subjectsDAO();
        context = application.getApplicationContext();
    }

    public GradesRepository(Context context) {
        Database database = Database.getInstance(context);
        gradesDAO = database.gradesDAO();
        subjectsDao = database.subjectsDAO();
        this.context = context;
    }

    public void insert(List<Grade> grades) {
        Database.databaseWriteExecutor.execute(() -> {
            int newGradesSize = 0;
            //grades.add(3, new Grade("Bio 4 - 2P","B-2P-ZI", "03.09.2020", 6.0f, 1.0f, 4,  0));
            //grades.remove(1);
            //grades.get(4).setGrade(6.0f);
            int oldGradesSize = gradesDAO.size();
            if (oldGradesSize != 0) { //oldGradesSize <= grades.size() &&
                List<Grade> oldGrades = gradesDAO.getAllGradesOrdered();
                List<Grade> newGrades = new ArrayList<>(grades);

                for (int i = 0; i < oldGrades.size(); i++) {
                    for (int k = 0; k < newGrades.size(); k++) {
                        k = newGradesSize;
                        if (oldGrades.get(i).compare(newGrades.get(k))) {
                            newGrades.remove(k);
                            oldGrades.remove(i);
                            i--;
                            break;
                        } else if (i+1 < oldGrades.size()) {
                            if (oldGrades.get(i+1).compare(newGrades.get(k))) {
                                newGrades.remove(k);
                                oldGrades.remove(i+1);
                                break;
                            }
                        }
                        newGradesSize++;
                    }
                }

                List<Grade> modifiedGrades = new ArrayList<>();

                for (int i = 0; i < oldGrades.size(); i++) {
                    for (int k = 0; k < newGrades.size(); k++) {
                        if (oldGrades.get(i).gradeModified(newGrades.get(k))) {
                            modifiedGrades.add(newGrades.get(k));
                            oldGrades.remove(i);
                            newGrades.remove(k);
                            i--;
                            break;
                        }
                    }
                }

                List<Notification> notificationList = new ArrayList<>();

                for (Grade grade : oldGrades) {
                    gradesDAO.deleteByGrade(grade.getSubjectId(), grade.getExam(), grade.getDate());
                    String deletedText = context.getString(R.string.deletedGrades1) + grade.getExam() + context.getString(R.string.deletedGrades2);

                    float subjectAverage = subjectsDao.getSubjectAverage(grade.getSubjectId());
                    float subjectPluspoints = subjectsDao.getSubjectPluspoints(grade.getSubjectId());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 0);
                    intent.putExtra("subjectID", grade.getSubjectId());
                    intent.putExtra("average", subjectAverage);
                    intent.putExtra("pluspoints", subjectPluspoints);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notificationDel = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(grade.getExam())
                            .setContentText(deletedText)
                            .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(context.getString(R.string.subject) + grade.getSubjectId())
                            .addLine(context.getString(R.string.exam) + grade.getExam())
                            .addLine(context.getString(R.string.gradeNot) + grade.getGrade())
                            .setBigContentTitle(deletedText))
                            .setSmallIcon(R.drawable.ktstgallen)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationList.add(notificationDel);
                }

                gradesDAO.insert(newGrades);
                for (Grade grade : newGrades) {
                    String addedText = context.getString(R.string.addedGrades1) + grade.getExam() + context.getString(R.string.addedGrades2);

                    float subjectAverage = subjectsDao.getSubjectAverage(grade.getSubjectId());
                    float subjectPluspoints = subjectsDao.getSubjectPluspoints(grade.getSubjectId());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 0);
                    intent.putExtra("subjectID", grade.getSubjectId());
                    intent.putExtra("average", subjectAverage);
                    intent.putExtra("pluspoints", subjectPluspoints);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notificationAdd = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(grade.getExam())
                            .setContentText(addedText)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(context.getString(R.string.subject) + grade.getSubjectId())
                                    .addLine(context.getString(R.string.exam) + grade.getExam())
                                    .addLine(context.getString(R.string.gradeNot) + grade.getGrade())
                                    .setBigContentTitle(addedText))
                            .setSmallIcon(R.drawable.ktstgallen)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationList.add(notificationAdd);
                }

                for (Grade grade : modifiedGrades) {
                    gradesDAO.updateGrade(grade.getSubjectId(), grade.getExam(), grade.getDate(), grade.getGrade(), grade.getWeight());
                    String moddedText = context.getString(R.string.modifiedGrades1) + grade.getExam() + context.getString(R.string.modifiedGrades2);

                    float subjectAverage = subjectsDao.getSubjectAverage(grade.getSubjectId());
                    float subjectPluspoints = subjectsDao.getSubjectPluspoints(grade.getSubjectId());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 0);
                    intent.putExtra("subjectID", grade.getSubjectId());
                    intent.putExtra("average", subjectAverage);
                    intent.putExtra("pluspoints", subjectPluspoints);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notificationMod = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(grade.getExam())
                            .setContentText(moddedText)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(context.getString(R.string.subject) + grade.getSubjectId())
                                    .addLine(context.getString(R.string.exam) + grade.getExam())
                                    .addLine(context.getString(R.string.gradeNot) + grade.getGrade())
                                    .setBigContentTitle(moddedText))
                            .setSmallIcon(R.drawable.ktstgallen)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationList.add(notificationMod);
                }

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                //notificationList.add(new NotificationCompat.Builder(context, App.CHANNEL_GRADES).setContentTitle("Worker triggered").setSmallIcon(R.drawable.ktstgallen).build());

                if (notificationList.size() < 10) {
                    for (int i = 0; i < notificationList.size(); i++) {
                        notificationManager.notify(i, notificationList.get(i));
                    }
                }

            } else {
                gradesDAO.insert(grades);
            }
        });
    }

    LiveData<List<Grade>> getBySubject(String passedSubject) {
        return gradesDAO.getBySubject(passedSubject);
    }

    public void deleteAll() {
        gradesDAO.deleteAll();
    }
}

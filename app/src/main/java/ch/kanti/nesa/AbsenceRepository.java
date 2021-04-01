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
import ch.kanti.nesa.daos.AbsenceDAO;
import ch.kanti.nesa.tables.Absence;

public class AbsenceRepository {
    AbsenceDAO absenceDAO;
    Context context;


    public AbsenceRepository(Application application) {
        Database database = Database.getInstance(application);
        absenceDAO = database.absenceDAO();
        context = application.getApplicationContext();
    }

    public AbsenceRepository(Context context) {
        Database database = Database.getInstance(context);
        absenceDAO = database.absenceDAO();
        this.context = context;
    }

    public void insert(List<Absence> absences) {
        Database.databaseWriteExecutor.execute(() -> {

            //absences.add(new Absence("10.03.2021", "13:00", "Physik", 0, 0));

           List<Absence> oldAbsences = absenceDAO.getAbsencesSync();

           if (oldAbsences.size() != 0) {
               for (int i = 0; i < oldAbsences.size(); i++) {
                   Absence oldAbs = oldAbsences.get(i);
                   for (int k = 0; k < absences.size(); k++) {
                       Absence newAbs = absences.get(k);
                       if (oldAbs.compare(newAbs)) {
                           absences.remove(k);
                           oldAbsences.remove(i);
                           i--;
                       }
                   }
               }

               List<Absence> modifiedAbsences = new ArrayList<>();

               for (int i = 0; i < oldAbsences.size(); i++) {
                   for (int k = 0; k < absences.size(); k++) {
                       if (oldAbsences.get(i).modified(absences.get(k))) {
                           modifiedAbsences.add(absences.get(k));
                           oldAbsences.remove(i);
                           absences.remove(k);
                           i--;
                           break;
                       }
                   }
               }

               List<Notification> notificationList = new ArrayList<>();

               for (Absence absence : oldAbsences) {
                   absenceDAO.deleteByAbsence(absence.getDate(), absence.getTime(), absence.getCourse(), absence.getType());
                   String deletedText = context.getString(R.string.absence1) + absence.getTime() + " - " + absence.getCourse() + context.getString(R.string.deletedAbsence2);

                   Intent intent = new Intent(context, MainActivity.class);
                   intent.putExtra("type", 1);

                   PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                   Notification notificationDel = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                           .setContentTitle(absence.getDate() + " - " + absence.getCourse())
                           .setContentText(deletedText)
                           .setStyle(new NotificationCompat.InboxStyle()
                                   .addLine(context.getString(R.string.date) + absence.getDate())
                                   .addLine(context.getString(R.string.course) + absence.getCourse())
                                   .setBigContentTitle(deletedText))
                           .setSmallIcon(R.drawable.ktstgallen)
                           .setContentIntent(pendingIntent)
                           .setAutoCancel(true)
                           .build();
                   notificationList.add(notificationDel);
               }

               absenceDAO.insertAll(absences);
               for (Absence absence : absences) {
                   String deletedText = context.getString(R.string.absence1) + absence.getTime() + " - " + absence.getCourse() + context.getString(R.string.addedAbsence2);

                   Intent intent = new Intent(context, MainActivity.class);
                   intent.putExtra("type", 1);

                   PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                   Notification notificationDel = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                           .setContentTitle(absence.getDate() + " - " + absence.getCourse())
                           .setContentText(deletedText)
                           .setStyle(new NotificationCompat.InboxStyle()
                                   .addLine(context.getString(R.string.dateNot) + absence.getDate())
                                   .addLine(context.getString(R.string.courseNot) + absence.getCourse())
                                   .setBigContentTitle(deletedText))
                           .setSmallIcon(R.drawable.ktstgallen)
                           .setContentIntent(pendingIntent)
                           .setAutoCancel(true)
                           .build();
                   notificationList.add(notificationDel);
               }


               for (Absence absence : modifiedAbsences) {
                   absenceDAO.updateAbsence(absence.getDate(), absence.getTime(), absence.getCourse(), absence.getType(), absence.getExcused());
                   String deletedText = context.getString(R.string.absence1) + absence.getTime() + " - " + absence.getCourse() + context.getString(R.string.modifiedAbsence2);

                   Intent intent = new Intent(context, MainActivity.class);
                   intent.putExtra("type", 1);

                   PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                   Notification notificationDel = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                           .setContentTitle(absence.getDate() + " - " + absence.getCourse())
                           .setContentText(deletedText)
                           .setStyle(new NotificationCompat.InboxStyle()
                                   .addLine(context.getString(R.string.date) + absence.getDate())
                                   .addLine(context.getString(R.string.course) + absence.getCourse())
                                   .setBigContentTitle(deletedText))
                           .setSmallIcon(R.drawable.ktstgallen)
                           .setContentIntent(pendingIntent)
                           .setAutoCancel(true)
                           .build();
                   notificationList.add(notificationDel);
               }

               NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

               if (notificationList.size() < 10) {
                   for (int i = 0; i < notificationList.size(); i++) {
                       notificationManager.notify(i, notificationList.get(i));
                   }
               }
           } else {
               absenceDAO.insertAll(absences);
           }
        });
    }

    public LiveData<List<Absence>> getAbsences() {
        return absenceDAO.getAbsences();
    }

    public LiveData<Integer> getAbsenceSize() {
        return absenceDAO.getAbsenceSize();
    }

    public void deleteAll() {
        absenceDAO.deleteAll();
    }
}

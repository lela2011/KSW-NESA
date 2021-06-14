package ch.kanti.nesa;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import ch.kanti.nesa.background.SyncWorker;

public class App extends Application {

    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";

    public static final String LOGIN_COMPLETED = "IS_LOGIN_COMPLETE";
    public static final String FIRST_LOGIN = "FIRST_LOGIN";

    public static final String CHANNEL_GRADES = "channel_grades";
    public static final String CHANNEL_ABSENCES = "channel_absences";
    public static final String CHANNEL_BANK = "channel_bank";

    public static final String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
    public static final String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";

    public static SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels();
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork("Sync Grade", ExistingPeriodicWorkPolicy.REPLACE, workRequest);

        sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationChannel gradesChannel = new NotificationChannel(
                CHANNEL_GRADES,
                getString(R.string.grades),
                NotificationManager.IMPORTANCE_HIGH);
        gradesChannel.enableVibration(true);
        gradesChannel.setDescription(getString(R.string.grades));

        NotificationChannel absencesChannel = new NotificationChannel(
                CHANNEL_ABSENCES,
                getString(R.string.absences),
                NotificationManager.IMPORTANCE_HIGH);
        absencesChannel.enableVibration(true);
        absencesChannel.setDescription(getString(R.string.absences));

        NotificationChannel bankChannel = new NotificationChannel(
                CHANNEL_BANK,
                getString(R.string.bank),
                NotificationManager.IMPORTANCE_HIGH);
        bankChannel.enableVibration(true);
        bankChannel.setDescription(getString(R.string.bank));

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(gradesChannel);
        manager.createNotificationChannel(bankChannel);
        manager.createNotificationChannel(absencesChannel);

    }

    public static int getLessonIndex(LocalTime time) {
        if (isAfterOrEqual(LocalTime.parse("00:00"), time) && time.isBefore(LocalTime.parse("07:40"))) {
            return 0;
        } else if (isAfterOrEqual(LocalTime.parse("07:40"), time) && time.isBefore(LocalTime.parse("08:30"))) {
            return 1;
        } else if (isAfterOrEqual(LocalTime.parse("08:30"), time) && time.isBefore(LocalTime.parse("09:35"))) {
            return 2;
        } else if (isAfterOrEqual(LocalTime.parse("09:35"), time) && time.isBefore(LocalTime.parse("10:25"))) {
            return 3;
        } else if (isAfterOrEqual(LocalTime.parse("10:25"), time) && time.isBefore(LocalTime.parse("11:20"))) {
            return 4;
        } else if (isAfterOrEqual(LocalTime.parse("11:20"), time) && time.isBefore(LocalTime.parse("12:10"))) {
            return 5;
        } else if (isAfterOrEqual(LocalTime.parse("12:10"), time) && time.isBefore(LocalTime.parse("13:00"))) {
            return 6;
        } else if (isAfterOrEqual(LocalTime.parse("13:00"), time) && time.isBefore(LocalTime.parse("13:50"))) {
            return 7;
        } else if (isAfterOrEqual(LocalTime.parse("13:50"), time) && time.isBefore(LocalTime.parse("14:45"))) {
            return 8;
        } else if (isAfterOrEqual(LocalTime.parse("14:45"), time) && time.isBefore(LocalTime.parse("15:35"))) {
            return 9;
        } else if (isAfterOrEqual(LocalTime.parse("15:35"), time) && time.isBefore(LocalTime.parse("16:30"))) {
            return 10;
        } else if (isAfterOrEqual(LocalTime.parse("16:30"), time) && time.isBefore(LocalTime.parse("17:20"))) {
            return 11;
        } else if (isAfterOrEqual(LocalTime.parse("17:20"), time) && time.isBefore(LocalTime.parse("18:00"))) {
            return 12;
        } else if (isAfterOrEqual(LocalTime.parse("18:00"), time) && time.isBefore(LocalTime.parse("19:00"))) {
            return 13;
        } else if (isAfterOrEqual(LocalTime.parse("19:00"), time) && time.isBefore(LocalTime.parse("20:00"))) {
            return 14;
        } else if (isAfterOrEqual(LocalTime.parse("20:00"), time) && time.isBefore(LocalTime.parse("21:00"))) {
            return 15;
        } else if (isAfterOrEqual(LocalTime.parse("21:00"), time) && time.isBefore(LocalTime.parse("22:00"))) {
            return 16;
        } else if (isAfterOrEqual(LocalTime.parse("22:00"), time)) {
            return -2;
        } else {
            return 0;
        }
    }

    public static boolean isAfterOrEqual(LocalTime time, LocalTime currentTime) {
        return currentTime.isAfter(time) || currentTime.equals(time);
    }
}

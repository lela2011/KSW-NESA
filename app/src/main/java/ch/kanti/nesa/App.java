package ch.kanti.nesa;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import ch.kanti.nesa.background.SyncWorker;

public class App extends Application {

    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";

    public static final String LOGIN_COMPLETED = "IS_LOGIN_COMPLETE";
    public static final String FIRST_LOGIN = "FIRST_LOGIN";

    public static final String CHANNEL_GRADES = "channel_grades";
    public static final String CHANNEL_ABSENCES = "channel_absences";
    public static final String CHANNEL_BANK = "channel_bank";

    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int LOGIN_ERROR = 2;
    public static final int LOGIN_FAILED = -1;

    public static final String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
    public static final String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";

    public static SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

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
}

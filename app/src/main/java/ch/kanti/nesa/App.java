package ch.kanti.nesa;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_GRADES = "channel_grades";
    public static final String CHANNEL_ABSENCES = "channel_absences";
    public static final String CHANNEL_BANK = "channel_bank";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
}

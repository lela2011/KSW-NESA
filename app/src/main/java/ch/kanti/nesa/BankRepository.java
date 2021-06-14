package ch.kanti.nesa;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;

import ch.kanti.nesa.activities.MainActivity;
import ch.kanti.nesa.daos.BankDAO;
import ch.kanti.nesa.tables.BankStatement;

import java.util.ArrayList;
import java.util.List;

public class BankRepository {
    final BankDAO bankDAO;
    final Context context;

    public BankRepository(Application application) {
        Database database = Database.getInstance(application);
        bankDAO = database.bankStatementDAO();
        context = application.getApplicationContext();
    }

    public BankRepository(Context context) {
        Database database = Database.getInstance(context);
        bankDAO = database.bankStatementDAO();
        this.context = context;
    }



    public void insert(List<BankStatement> statement) {
        Database.databaseWriteExecutor.execute(()-> {
            if(statement.size() != 0) {
                //statement.add(new BankStatement("pk", 16, "11.03.2021", "Test", 10, 69420));
                List<BankStatement> oldBank = bankDAO.getBankStatementSync();
                for (int i = 0; i < oldBank.size(); i++) {
                    for (int k = 0; k < statement.size(); k++) {
                        if (oldBank.get(i).compare(statement.get(k))) {
                            statement.remove(k);
                            oldBank.remove(i);
                            i--;
                            break;
                        }
                    }
                }

                List<BankStatement> modifiedBank = new ArrayList<>();

                for (int j = 0; j < oldBank.size(); j++) {
                    for (int k = 0; k < statement.size(); k++) {
                        if (oldBank.get(j).bankModified(statement.get(k))) {
                            modifiedBank.add(statement.get(k));
                            oldBank.remove(j);
                            statement.remove(k);
                            j--;
                            break;
                        }
                    }
                }

                List<Notification> notificationList = new ArrayList<>();

                for (BankStatement bank : oldBank) {
                    bankDAO.deleteByStatement(bank.getPk());
                    String deletedText = context.getString(R.string.bank1) + bank.getTitle() + context.getString(R.string.deletedBank2);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 2);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notificationDel = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(bank.getTitle())
                            .setContentText(deletedText)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(context.getString(R.string.bankName) + bank.getTitle())
                                    .addLine(context.getString(R.string.amountBank) + bank.getAmount())
                                    .addLine(context.getString(R.string.balanceBank) + bank.getBalance())
                                    .setBigContentTitle(deletedText))
                            .setSmallIcon(R.drawable.ktstgallen)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationList.add(notificationDel);
                }

                bankDAO.insert(statement);
                for (BankStatement newBank : statement) {
                    String addedText = context.getString(R.string.bank1) + newBank.getTitle() + context.getString(R.string.addedBank2);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 2);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notificationDel = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(newBank.getTitle())
                            .setContentText(addedText)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(context.getString(R.string.bankName) + newBank.getTitle())
                                    .addLine(context.getString(R.string.amountBank) + newBank.getAmount())
                                    .addLine(context.getString(R.string.balanceBank) + newBank.getBalance())
                                    .setBigContentTitle(addedText))
                            .setSmallIcon(R.drawable.ktstgallen)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationList.add(notificationDel);
                }


                for (BankStatement bank : modifiedBank) {
                    bankDAO.updateByStatement(bank.getDate(), bank.getTitle(), bank.getAmount(), bank.getBalance());
                    String moddedText = context.getString(R.string.bank1) + bank.getTitle() + context.getString(R.string.modifiedBank2);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 2);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notificationMod = new NotificationCompat.Builder(context, App.CHANNEL_GRADES)
                            .setContentTitle(bank.getTitle())
                            .setContentText(moddedText)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(context.getString(R.string.bankName) + bank.getTitle())
                                    .addLine(context.getString(R.string.amountBank) + bank.getAmount())
                                    .addLine(context.getString(R.string.balanceBank) + bank.getBalance())
                                    .setBigContentTitle(moddedText))
                            .setSmallIcon(R.drawable.ktstgallen)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationList.add(notificationMod);
                }

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                if (notificationList.size() < 10) {
                    for (int l = 0; l < notificationList.size(); l++) {
                        notificationManager.notify(l, notificationList.get(l));
                    }
                }
            } else {
                bankDAO.insert(statement);
            }
        });
    }

    public void deleteAll() {
        Database.databaseWriteExecutor.execute(bankDAO::deleteAll);
    }

    LiveData<List<BankStatement>> getBankStatement() {
        return bankDAO.getBankStatement();
    }

    LiveData<Float> getBalance() {
        return bankDAO.getBalance();
    }
}
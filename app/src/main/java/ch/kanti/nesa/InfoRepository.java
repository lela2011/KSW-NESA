package ch.kanti.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import ch.kanti.nesa.daos.AccountInfoDAO;
import ch.kanti.nesa.tables.AccountInfo;

import java.util.ArrayList;
import java.util.List;

public class InfoRepository {

    AccountInfoDAO accountInfoDAO;

    public InfoRepository(Application application) {
        Database database = Database.getInstance(application);
        accountInfoDAO = database.accountInfoDAO();
    }

    //TODO: Work on Info Repository
    public void insert(List<AccountInfo> info){
        Database.databaseWriteExecutor.execute(() -> {
            List<AccountInfo> oldData = accountInfoDAO.getAccountInfo();
            if(oldData.size() == 0) {
                accountInfoDAO.insertAll(info);
            } else {
                List<AccountInfo> newData = new ArrayList<>();
                for (int i = 0; i < oldData.size(); i++) {
                    if (!(info.get(i).value.equals(oldData.get(i).value))){
                        newData.add(info.get(i));
                    }
                }
                if(newData.size() != 0) {
                    accountInfoDAO.insertAll(newData);
                }
            }
        });
    }

    public LiveData<List<AccountInfo>> getAccountInfo() {
        return accountInfoDAO.getAccountInfoLive();
    }

    public void deleteAll() {
        accountInfoDAO.deleteAll();
    }
}

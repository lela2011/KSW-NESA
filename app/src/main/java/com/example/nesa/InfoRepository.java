package com.example.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.AccountInfoDAO;
import com.example.nesa.tables.AccountInfo;

import java.util.ArrayList;
import java.util.List;

public class InfoRepository {

    AccountInfoDAO accountInfoDAO;

    public InfoRepository(Application application) {
        Database database = Database.getInstance(application);
        accountInfoDAO = database.accountInfoDAO();
    }

    public void update(List<AccountInfo> info){
        Database.databaseWriteExecutor.execute(()->{
            accountInfoDAO.update(info);
        });
    }

    public void insert(List<AccountInfo> info){
        Database.databaseWriteExecutor.execute(()->{
            accountInfoDAO.insert(info);
        });
    }

    public void delete(AccountInfo info){
        Database.databaseWriteExecutor.execute(()->{
            accountInfoDAO.delete(info);
        });
    }

    public LiveData<List<AccountInfo>> getAccountInfo() {
        return accountInfoDAO.getAccountInfo();
    }

    public LiveData<Integer> getTableSize() {
        return accountInfoDAO.getTableSize();
    }
}

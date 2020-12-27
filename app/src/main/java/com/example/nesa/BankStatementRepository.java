package com.example.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.BankStatementDAO;
import com.example.nesa.tables.BankStatement;

import java.util.List;

public class BankStatementRepository {
    BankStatementDAO bankStatementDAO;

    public BankStatementRepository(Application application) {
        Database database = Database.getInstance(application);
        bankStatementDAO = database.bankStatementDAO();
    }

    public void update(List<BankStatement> statement) {
        Database.databaseWriteExecutor.execute(() -> bankStatementDAO.update(statement));
    }

    public void insertAll(List<BankStatement> statement) {
        Database.databaseWriteExecutor.execute(()-> bankStatementDAO.insertAll(statement));
    }

    public void insert(BankStatement statement) {
        Database.databaseWriteExecutor.execute(()-> bankStatementDAO.insert(statement));
    }

    public void deleteAll() {
        Database.databaseWriteExecutor.execute(()-> bankStatementDAO.deleteAll());
    }

    LiveData<List<BankStatement>> getBankStatement() {
        return bankStatementDAO.getBankStatement();
    }

    LiveData<Float> getBalance() {
        return bankStatementDAO.getBalance();
    }
}

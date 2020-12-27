package com.example.nesa;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.nesa.daos.BankDAO;
import com.example.nesa.tables.BankStatement;

import java.util.List;

public class BankRepository {
    BankDAO bankDAO;

    public BankRepository(Application application) {
        Database database = Database.getInstance(application);
        bankDAO = database.bankStatementDAO();
    }

    public void insert(List<BankStatement> statement) {
        Database.databaseWriteExecutor.execute(()-> {
            if (bankDAO.size() > statement.size()){
                bankDAO.deleteAll();
            }
            bankDAO.insert(statement);
        });
    }

    public void deleteAll() {
        Database.databaseWriteExecutor.execute(()-> bankDAO.deleteAll());
    }

    LiveData<List<BankStatement>> getBankStatement() {
        return bankDAO.getBankStatement();
    }

    LiveData<Float> getBalance() {
        return bankDAO.getBalance();
    }
}

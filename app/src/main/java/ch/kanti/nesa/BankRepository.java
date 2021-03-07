package ch.kanti.nesa;

import android.app.Application;
import android.content.Context;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;

import ch.kanti.nesa.daos.BankDAO;
import ch.kanti.nesa.tables.BankStatement;

import java.util.List;

public class BankRepository {
    BankDAO bankDAO;
    Context context;

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
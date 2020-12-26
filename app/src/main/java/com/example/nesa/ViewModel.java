package com.example.nesa;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.BankStatement;
import com.example.nesa.tables.User;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final LoginRepository loginRepository;
    private final InfoRepository infoRepository;
    private final BankStatementRepository bankRepository;
    //initialize repository
    public ViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new LoginRepository(application);
        infoRepository = new InfoRepository(application);
        bankRepository = new BankStatementRepository(application);
    }
    //update entry
    public void updateLogin(User user) {
        loginRepository.update(user);
    }
    //insert entry
    public void insertLogin(User user) {
        loginRepository.insert(user);
    }
    //delete entry
    public void deleteLogin(User user) {
        loginRepository.delete(user);
    }
    //get credentials
    public LiveData<User> getCredentials() {
        return loginRepository.getCredentials();
    }
    //get table size
    public LiveData<Integer> getTableSizeLogin() {
        return loginRepository.getTableSize();
    }

    public void updateInfo(List<AccountInfo> info) {
        infoRepository.update(info);
    }

    public void insertInfo(List<AccountInfo> info) {
        infoRepository.insert(info);
    }

    public void deleteInfo(AccountInfo info) {
        infoRepository.delete(info);
    }

    public LiveData<List<AccountInfo>> getAccountInfo() {
        return infoRepository.getAccountInfo();
    }

    public LiveData<Integer> getTableSizeInfo() {
        return infoRepository.getTableSize();
    }

    public void updateBank(List<BankStatement> statement){
        bankRepository.update(statement);
    }

    public void insertAllBank(List<BankStatement> statement) {
        bankRepository.insertAll(statement);
    }

    public void insertBank(BankStatement statement) {
        bankRepository.insert(statement);
    }

    public void deleteAllBank() {
        bankRepository.deleteAll();
    }

    public LiveData<List<BankStatement>> getBankStatements() {
        return bankRepository.getBankStatement();
    }

    public LiveData<Float> getBalance(){
        return bankRepository.getBalance();
    }
}
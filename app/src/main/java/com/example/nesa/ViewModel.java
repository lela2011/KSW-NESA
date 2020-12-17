package com.example.nesa;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.nesa.tables.AccountInfo;
import com.example.nesa.tables.User;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private LoginRepository loginRepository;
    private InfoRepository infoRepository;
    //initialize repository
    public ViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new LoginRepository(application);
        infoRepository = new InfoRepository(application);
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
}
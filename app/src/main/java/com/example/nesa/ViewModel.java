package com.example.nesa;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private Repository repository;
    //initialize repository
    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }
    //update entry
    public void update(User user) {
        repository.update(user);
    }
    //insert entry
    public void insert(User user) {
        repository.insert(user);
    }
    //delete entry
    public void delete(User user) {
        repository.delete(user);
    }
    //get credentials
    public LiveData<User> getCredentials() {
        return repository.getCredentials();
    }
    //get table size
    public LiveData<Integer> getTableSize() {
        return repository.getTableSize();
    }
}
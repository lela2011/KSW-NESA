package com.example.nesa;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoginHandler {

    public static int checkLoginCredentials(String encryptedUsername, String encryptedPassword) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> result = executorService.submit(new LoginCredentialChecker(encryptedUsername, encryptedPassword));
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return LoginActivity.LOGIN_ERROR;
    }
}
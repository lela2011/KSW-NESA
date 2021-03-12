package ch.kanti.nesa.background;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.kanti.nesa.activities.LoginActivity;

public class LoginHandler {
    //run credential checking on background thread
    public static int checkLoginCredentials(String encryptedUsername, String encryptedPassword) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
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

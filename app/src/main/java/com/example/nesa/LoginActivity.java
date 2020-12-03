package com.example.nesa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nesa.databinding.LoginActivityBinding;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class LoginActivity extends AppCompatActivity {

    LoginActivityBinding binding;
    AlertDialog.Builder dialogBuilder;
    public static final int INTERNET_REQUEST = 1;
    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int LOGIN_ERROR = 2;
    public static final int LOGIN_FAILED = -1;
    public static final String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
    public static final String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkInternetPermission();

        dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(R.string.internet_dialog_message)
                .setTitle(R.string.internet_dialog_title)
                .setPositiveButton(R.string.dialogButtonOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{Manifest.permission.INTERNET}, INTERNET_REQUEST);
                    }
                })
                .setNegativeButton(R.string.dialogButtonCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        binding.usernameET.setEnabled(false);
                        binding.usernameET.setFocusable(false);

                        binding.passwordET.setEnabled(false);
                        binding.passwordET.setFocusable(false);

                        binding.loginSubmit.setEnabled(false);
                    }
                }).create();

        binding.hideShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // code for hide and show password button
            }
        });

        binding.passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    try {
                        getLoginCredentials();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        binding.loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getLoginCredentials();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getLoginCredentials() throws ExecutionException, InterruptedException {
        String encryptedUsername = AES.encrypt(binding.usernameET.getText().toString(), usernameKey);
        String encryptedPassword = AES.encrypt(binding.passwordET.getText().toString(), passwordKey);
        Toast.makeText(this, "Username:\n" + encryptedUsername + "\nPassword:\n" + encryptedPassword, Toast.LENGTH_SHORT).show();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            int loginResultCode = LoginHandler.checkLoginCredentials(encryptedUsername, encryptedPassword);
            runOnUiThread(()->{
                if(loginResultCode == LOGIN_FAILED){
                    binding.loginErrorMessage.setText(R.string.loginErrorCredentials);
                    binding.loginErrorMessage.setTextColor(getColor(R.color.errorRed));
                } else if(loginResultCode == LOGIN_ERROR){
                    binding.loginErrorMessage.setText(R.string.loginErrorFailed);
                    binding.loginErrorMessage.setTextColor(getColor(R.color.errorRed));
                } else if(loginResultCode == LOGIN_SUCCESSFUL){
                    binding.loginErrorMessage.setText(R.string.loginSuccessful);
                    binding.loginErrorMessage.setTextColor(getColor(R.color.secondaryDarkColor));
                }
            });
        });
    }

    private void checkInternetPermission() {
        //Log.d("Permission", "Method called");
        if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            //Log.d("Permission", "Permission denied");
            if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
                runOnUiThread(() -> {
                    dialogBuilder.show();
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INTERNET_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission to internet granted", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void insertCredentials(String username, String password) {
        // code for inserting credentials in to database
    }
}
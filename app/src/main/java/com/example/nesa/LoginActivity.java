package com.example.nesa;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nesa.databinding.LoginActivityBinding;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginActivity extends AppCompatActivity {

    //Variable definition
    LoginActivityBinding binding;
    AlertDialog.Builder dialogBuilder;
    public static final int INTERNET_REQUEST = 1;
    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int LOGIN_ERROR = 2;
    public static final int LOGIN_FAILED = -1;
    //Keys for encryption
    public static final String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
    public static final String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";

    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Initialize ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        //Check for internet permission on device
        checkInternetPermission();
        //Create dialog to explain why internet permission is needed
        dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(R.string.internet_dialog_message)
                .setTitle(R.string.internet_dialog_title)
                .setPositiveButton(R.string.dialogButtonOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{Manifest.permission.INTERNET}, INTERNET_REQUEST);
                    }
                }).create();

        //showing and hiding password
        binding.showHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.showHidePwd.getContentDescription().equals("show_btn")) {
                    binding.passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.showHidePwd.setContentDescription("hide_btn");
                    binding.showHidePwd.setImageResource(R.drawable.hide_pwd);
                } else {
                    binding.passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.showHidePwd.setContentDescription("show_btn");
                    binding.showHidePwd.setImageResource(R.drawable.show_pwd);
                }
            }
        });

        //pressing enter or login to post username and password
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
        //save credentials on button press
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

    //Reading out credentials, encrypting, checking for validity, writing into database
    private void getLoginCredentials() throws ExecutionException, InterruptedException {
        //close keyboard on screen
        closeKeyboard();
        //encrypt username and password
        String encryptedUsername = AES.encrypt(binding.usernameET.getText().toString(), usernameKey);
        String encryptedPassword = AES.encrypt(binding.passwordET.getText().toString(), passwordKey);
        //clear the input fields
        binding.usernameET.getText().clear();
        binding.passwordET.getText().clear();
        //check if credentials are correct
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            //get return value from checking credentials
            int loginResultCode = LoginHandler.checkLoginCredentials(encryptedUsername, encryptedPassword);
            //displaying results
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
                    //check if user already saved in database
                    checkTableSize(encryptedUsername, encryptedPassword);
                    //Set login completed
                    SplashActivity.editor.putBoolean(SplashActivity.LOGIN_COMPLETED, true);
                    SplashActivity.editor.apply();
                    //Start main Activity
                    Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            });
        });
    }
    //check if there already is a user added to database
    public void checkTableSize(String username, String password){
        viewModel.getTableSize().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 0){
                    //add new user
                    insertCredentials(username, password);
                } else {
                    //update credentials
                    updateCredentials(username, password);
                }
            }
        });
    }
    //add user to database
    public void insertCredentials(String username, String password) {
        User user = new User(username, password);
        viewModel.insert(user);
    }
    //update credentials
    public void updateCredentials(String username, String password){
        //get saved credentials out of database
        viewModel.getCredentials().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User users) {
                //check if credentials changed
                if(!(users.getUsername().equals(username) && users.getPassword().equals(password))){
                    User user = new User(username, password);
                    user.setId(users.getId());
                    //update credentials
                    viewModel.update(user);
                }
            }
        });
    }

    //check internet permission
    private void checkInternetPermission() {
        //check if permission already granted
        if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            //check if PermissionRationale should be shown
            if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
                runOnUiThread(() -> {
                    //show modal
                    dialogBuilder.show();
                });
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //checking if request was about internet
        if (requestCode == INTERNET_REQUEST) {
            //checking if permission was granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission to internet granted", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Closing keyboard when input is finished
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.LoginActivityBinding;
import ch.kanti.nesa.background.LoginHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginActivity extends AppCompatActivity {

    //Variable definition
    LoginActivityBinding binding;

    String department;
    //Keys for encryption

    public static ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        //showing and hiding password
        binding.showHidePwd.setOnClickListener(view1 -> {
            if (binding.showHidePwd.getContentDescription().equals("show_btn")) {
                binding.passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.showHidePwd.setContentDescription("hide_btn");
                binding.showHidePwd.setImageResource(R.drawable.hide_pwd);
            } else {
                binding.passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.showHidePwd.setContentDescription("show_btn");
                binding.showHidePwd.setImageResource(R.drawable.show_pwd);
            }
        });

        //pressing enter or login to post username and password
        binding.passwordET.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                getLoginCredentials();
            }
            return false;
        });
        //save credentials on button press
        binding.loginSubmit.setOnClickListener(v -> getLoginCredentials());

        Spinner spinner = binding.fmsorgymmispinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    //Reading out credentials, encrypting, checking for validity, writing into database
    @SuppressLint("ApplySharedPref")
    private void getLoginCredentials() {
        //close keyboard on screen
        closeKeyboard();
        //encrypt username and password
        String encryptedUsername = AES.encrypt(binding.usernameET.getText().toString(), App.usernameKey);
        String encryptedPassword = AES.encrypt(binding.passwordET.getText().toString(), App.passwordKey);
        department = binding.fmsorgymmispinner.getSelectedItem().toString();
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
                if(loginResultCode == App.LOGIN_FAILED){
                    binding.loginErrorMessage.setText(R.string.loginErrorCredentials);
                    binding.loginErrorMessage.setTextColor(getColor(R.color.errorRed));
                } else if(loginResultCode == App.LOGIN_ERROR){
                    binding.loginErrorMessage.setText(R.string.loginErrorFailed);
                    binding.loginErrorMessage.setTextColor(getColor(R.color.errorRed));
                } else if(loginResultCode == App.LOGIN_SUCCESSFUL){
                    binding.loginErrorMessage.setText(R.string.loginSuccessful);
                    binding.loginErrorMessage.setTextColor(getColor(R.color.secondaryColorVariant));
                    //Set login completed
                }
            });
            if (loginResultCode == App.LOGIN_SUCCESSFUL) {
                App.sharedPreferences.edit().putBoolean(App.LOGIN_COMPLETED, true).commit();
                App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, true).commit();
                App.sharedPreferences.edit().putString("department", department).commit();
                App.sharedPreferences.edit().putString("username", encryptedUsername).commit();
                App.sharedPreferences.edit().putString("password", encryptedPassword).commit();
                //Start splash Activity
                Intent splashActivity = new Intent(this, SplashActivity.class);
                startActivity(splashActivity);
                finish();
            }
        });

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
package ch.kanti.nesa.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.AES;
import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.LoginActivityBinding;
import ch.kanti.nesa.objects.LoginAndScrape;
import ch.kanti.nesa.scrapers.Network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginActivity extends AppCompatActivity {

    //Variable definition
    LoginActivityBinding binding;

    String department;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme = App.sharedPreferences.getInt("theme", 0);
        int border = App.sharedPreferences.getInt("border", 0);

        if (theme == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (theme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        if(border == 0) {
            setTheme(R.style.Theme_NESA);
        } else if (border == 1) {
            setTheme(R.style.Theme_NESA_OLED);
        }

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

        //save credentials on button press
        binding.loginSubmit.setOnClickListener(v -> executorService.execute(this::getLoginCredentials));

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
        String username = AES.encrypt(binding.usernameET.getText().toString(), App.usernameKey);
        String password = AES.encrypt(binding.passwordET.getText().toString(), App.passwordKey);
        department = binding.fmsorgymmispinner.getSelectedItem().toString();
        //clear the input fields
        binding.usernameET.getText().clear();
        binding.passwordET.getText().clear();

        LoginAndScrape loginAndScrape = Network.checkLoginAndPages(false, false, false, false, username, password);

        runOnUiThread(()->{
            if(!loginAndScrape.isLoginCorrect() && loginAndScrape.isCheckSuccessful()){
                binding.loginErrorMessage.setText(R.string.loginErrorCredentials);
                binding.loginErrorMessage.setTextColor(getColor(R.color.errorRed));
            } else if(!loginAndScrape.isCheckSuccessful()){
                binding.loginErrorMessage.setText(R.string.loginErrorFailed);
                binding.loginErrorMessage.setTextColor(getColor(R.color.errorRed));
            } else if(loginAndScrape.isLoginCorrect() && loginAndScrape.isCheckSuccessful()){
                binding.loginErrorMessage.setText(R.string.loginSuccessful);
                binding.loginErrorMessage.setTextColor(getColor(R.color.secondaryColorVariant));
                //Set login completed
            }
        });

        if (loginAndScrape.isLoginCorrect() && loginAndScrape.isCheckSuccessful()) {
            App.sharedPreferences.edit().putBoolean(App.LOGIN_COMPLETED, true).apply();
            App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, true).apply();
            App.sharedPreferences.edit().putString("department", department).apply();
            App.sharedPreferences.edit().putString("username", username).apply();
            App.sharedPreferences.edit().putString("password", password).apply();
            //Start splash Activity
            Intent splashActivity = new Intent(this, SplashActivity.class);
            startActivity(splashActivity);
            finish();
        }
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
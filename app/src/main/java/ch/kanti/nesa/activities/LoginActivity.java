package ch.kanti.nesa.activities;

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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.LoginActivityBinding;
import ch.kanti.nesa.background.LoginHandler;
import ch.kanti.nesa.tables.User;

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
    private void getLoginCredentials() {
        //close keyboard on screen
        closeKeyboard();
        //encrypt username and password
        String encryptedUsername = User.encrypt(binding.usernameET.getText().toString(), App.usernameKey);
        String encryptedPassword = User.encrypt(binding.passwordET.getText().toString(), App.passwordKey);
        department = binding.fmsorgymmispinner.getSelectedItem().toString();
        if (department.isEmpty()) {
            Toast.makeText(this, "Please select department", Toast.LENGTH_SHORT).show();
        } else {
            SplashActivity.sharedPreferences.edit().putString("department", department).apply();
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
                        binding.loginErrorMessage.setTextColor(getColor(R.color.secondaryColorVariant));
                        //check if user already saved in database
                        checkTableSize(encryptedUsername, encryptedPassword);
                        //Set login completed
                        SplashActivity.editor.putBoolean(SplashActivity.LOGIN_COMPLETED, true);
                        SplashActivity.editor.putBoolean(SplashActivity.FIRST_LOGIN, true);
                        SplashActivity.editor.apply();
                        //Start main Activity
                        Intent splashActivity = new Intent(LoginActivity.this, SplashActivity.class);
                        startActivity(splashActivity);
                        finish();
                    }
                });
            });
        }

    }
    //check if there already is a user added to database
    public void checkTableSize(String username, String password){
        viewModel.getTableSizeLogin().observe(this, integer -> {
            if(integer == 0){
                //add new user
                insertCredentials(username, password);
            } else {
                //update credentials
                updateCredentials(username, password);
            }
        });
    }
    //add user to database
    public void insertCredentials(String username, String password) {
        User user = new User(username, password, department);
        viewModel.insertLogin(user);
    }
    //update credentials
    public void updateCredentials(String username, String password){
        //get saved credentials out of database
        viewModel.getCredentials().observe(this, users -> {
            //check if credentials changed
            if(!(users.getUsername().equals(username) && users.getPassword().equals(password))){
                User user = new User(username, password, department);
                user.setId(users.getId());
                //update credentials
                viewModel.updateLogin(user);
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
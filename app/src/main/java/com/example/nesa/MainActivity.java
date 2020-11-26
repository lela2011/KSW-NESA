package com.example.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nesa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    getLoginCredentials();
                }
                return false;
            }
        });
        binding.loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoginCredentials();
            }
        });
    }

    private void getLoginCredentials(){
        final String usernameKey = "eThWmZq4t7w!z%C*F-J@NcRfUjXn2r5u";
        final String passwordKey = "C*F-JaNdRgUjXn2r5u8x/A?D(G+KbPeS";
        String encryptedUsername = AES.encrypt(binding.usernameET.getText().toString(), usernameKey);
        String encryptedPassword = AES.encrypt(binding.passwordET.getText().toString(), passwordKey);
        Toast.makeText(this, "Username:\n" + encryptedUsername + "\nPassword:\n" + encryptedPassword, Toast.LENGTH_SHORT).show();
    }
}
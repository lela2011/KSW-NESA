package ch.kanti.nesa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.ActivityDetailedBankStatementBinding;

public class DetailedBankStatement extends AppCompatActivity {

    ActivityDetailedBankStatementBinding binding;

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

        binding = ActivityDetailedBankStatementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String date = intent.getStringExtra("Date");
        String description = intent.getStringExtra("Description");
        float amount = intent.getFloatExtra("Amount", 0);
        float balance = intent.getFloatExtra("Balance", 0);
        String amountString = amount + " " + getString(R.string.chf);
        String balanceString = balance + " " + getString(R.string.chf);

        binding.date.setText(date);
        binding.description.setText(description);
        binding.amount.setText(amountString);
        if(amount>0){
            binding.amount.setTextColor(getColor(R.color.green));
        }
        binding.balance.setText(balanceString);
        if(balance >= 100){
            binding.balance.setTextColor(getColor(R.color.green));
        } else if(balance > 0) {
            binding.balance.setTextColor(getColor(R.color.orange));
        } else {
            binding.balance.setTextColor(getColor(R.color.red));
        }
    }
}
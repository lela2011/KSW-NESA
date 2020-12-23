package com.example.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.nesa.databinding.ActivityDetailedBankStatementBinding;

public class DetailedBankStatement extends AppCompatActivity {

    ActivityDetailedBankStatementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
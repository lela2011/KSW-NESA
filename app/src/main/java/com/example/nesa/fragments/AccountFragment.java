package com.example.nesa.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nesa.BankAdapter;
import com.example.nesa.DetailedBankStatement;
import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    public FragmentAccountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        BankAdapter adapter = new BankAdapter();
        recyclerView.setAdapter(adapter);

        MainActivity.viewModel.getBankStatements().observe(getActivity(), statements -> {
            adapter.setStatements(statements);
            float balanceFloat = statements.get(statements.size()-1).saldo;
            @SuppressLint("DefaultLocale") String balance = String.format("%.2f",balanceFloat);
            binding.balance.setText(balance);
            if(balanceFloat >= 100){
                binding.balance.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
            } else if(balanceFloat > 0) {
                binding.balance.setTextColor(ContextCompat.getColor(getActivity(), R.color.orange));
            } else {
                binding.balance.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            }
        });

        adapter.setOnItemClickListener(statement -> {
            Intent intent = new Intent(getActivity(), DetailedBankStatement.class);
            intent.putExtra("Date", statement.date);
            intent.putExtra("Description", statement.title);
            intent.putExtra("Amount", statement.amount);
            intent.putExtra("Balance", statement.saldo);
            startActivity(intent);
        });

    }



}

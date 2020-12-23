package com.example.nesa.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nesa.AES;
import com.example.nesa.BankAdapter;
import com.example.nesa.CookieAndAuth;
import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.SplashActivity;
import com.example.nesa.databinding.FragmentAccountBinding;
import com.example.nesa.scrapers.CookieAndAuthScraper;
import com.example.nesa.scrapers.PageScraper;
import com.example.nesa.tables.BankStatement;
import com.example.nesa.tables.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        MainActivity.viewModel.getBankStatements().observe(getActivity(), new Observer<List<BankStatement>>() {
            @Override
            public void onChanged(List<BankStatement> statements) {
                adapter.setStatements(statements);
                float balanceFloat = statements.get(statements.size()-1).saldo;
                String balance = String.valueOf(balanceFloat + " " + getString(R.string.chf));
                binding.balance.setText(balance);
                if(balanceFloat >= 100){
                    binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                } else if(balanceFloat > 0) {
                    binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                } else {
                    binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
            }
        });
    }



}

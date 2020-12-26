package com.example.nesa.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.ViewModel;
import com.example.nesa.databinding.FragmentHomeBinding;
import com.example.nesa.tables.AccountInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class HomeFragment extends Fragment {

    public FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        ArrayList<TextView> personalInfoTextViews = new ArrayList<>(8);
        personalInfoTextViews.add(binding.name);
        personalInfoTextViews.add(binding.address);
        personalInfoTextViews.add(binding.city);
        personalInfoTextViews.add(binding.birthdate);
        personalInfoTextViews.add(binding.major);
        personalInfoTextViews.add(binding.familyorigin);
        personalInfoTextViews.add(binding.phone);
        personalInfoTextViews.add(binding.mobilephone);

        MainActivity.viewModel.getAccountInfo().observe(getActivity(), new Observer<List<AccountInfo>>() {
            @Override
            public void onChanged(List<AccountInfo> accountInfos) {
                if(accountInfos.size() == 8 && personalInfoTextViews.size() == 8){
                    for(int i = 0; i<8; i++){
                        personalInfoTextViews.get(i).setText(accountInfos.get(i).value);
                    }
                }
            }
        });

        MainActivity.viewModel.getBalance().observe(getActivity(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                if(aFloat != null){
                    String balance = aFloat + " " + getString(R.string.chf);
                    binding.balance.setText(balance);
                    if(aFloat >= 100){
                        binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                    } else if(aFloat > 0) {
                        binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                    } else {
                        binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    }
                }
            }
        });
    }
}

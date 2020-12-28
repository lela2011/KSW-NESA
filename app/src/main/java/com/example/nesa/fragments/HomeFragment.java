package com.example.nesa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.databinding.FragmentHomeBinding;

import java.util.ArrayList;

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
        personalInfoTextViews.add(binding.schoolMail);
        personalInfoTextViews.add(binding.privatemail);

        MainActivity.viewModel.getAccountInfo().observe(getActivity(), accountInfos -> {
            for(int i = 0; i < accountInfos.size(); i++){
                    personalInfoTextViews.get(i).setText(accountInfos.get(i).value);
            }
        });

        MainActivity.viewModel.getBalance().observe(getActivity(), aFloat -> {
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
        });
    }
}

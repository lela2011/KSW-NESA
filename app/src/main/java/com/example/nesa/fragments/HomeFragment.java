package com.example.nesa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.ViewModel;
import com.example.nesa.databinding.FragmentHomeBinding;
import com.example.nesa.tables.AccountInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class HomeFragment extends Fragment {

    public FragmentHomeBinding binding;
    ViewModel viewModel;

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
                if(accountInfos.size() != 0){
                    for(int i = 0; i<8; i++){
                        personalInfoTextViews.get(i).setText(accountInfos.get(i).value);
                    }
                }
            }
        });
    }
}

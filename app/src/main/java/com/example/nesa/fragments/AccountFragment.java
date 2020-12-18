package com.example.nesa.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.nesa.AES;
import com.example.nesa.CookieAndAuth;
import com.example.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.SplashActivity;
import com.example.nesa.databinding.FragmentAccountBinding;
import com.example.nesa.scrapers.CookieAndAuthScraper;
import com.example.nesa.scrapers.PageScraper;
import com.example.nesa.tables.User;

import org.jsoup.nodes.Document;

import java.util.HashMap;
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
}

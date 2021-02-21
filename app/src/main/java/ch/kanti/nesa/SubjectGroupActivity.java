package ch.kanti.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ch.kanti.nesa.databinding.ActivitySubjectGroupBinding;

public class SubjectGroupActivity extends AppCompatActivity {

    ActivitySubjectGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
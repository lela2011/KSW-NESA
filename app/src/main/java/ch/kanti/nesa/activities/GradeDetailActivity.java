package ch.kanti.nesa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.ActivityGradeDetailBinding;

public class GradeDetailActivity extends AppCompatActivity {
    public ActivityGradeDetailBinding binding;


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

        binding = ActivityGradeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String name = intent.getStringExtra("name");
        float weight = intent.getFloatExtra("weight", -1);
        float grade = intent.getFloatExtra("grade", -1);

        binding.description.setText(name);
        String weightString = "-";
        String gradeString = "-";
        String dateString = "-";
        if (weight != -1f) {
            weightString = String.valueOf(weight);
        }
        if (grade != -1f) {
            gradeString = String.valueOf(grade);
        }
        if (!date.isEmpty()) {
            dateString = date;
        }
        binding.date.setText(dateString);
        binding.weight.setText(weightString);
        binding.grade.setText(gradeString);
    }
}
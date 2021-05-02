package ch.kanti.nesa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.ActivityDetailedStudentBinding;

public class ActivityDetailedStudent extends AppCompatActivity {

    ActivityDetailedStudentBinding binding;

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

        binding = ActivityDetailedStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        String major = intent.getStringExtra("major");
        String course = intent.getStringExtra("course");
        String bilingual = intent.getStringExtra("bilingual");
        String gender = intent.getStringExtra("gender");
        String addCourses = intent.getStringExtra("addCourses");
        String status = intent.getStringExtra("status");

        String majorText = major + " - " + course;
        if(!bilingual.equals("")) {
            majorText += String.format(" - %s", bilingual);
        }
        binding.name.setText(name);
        binding.address.setText(address.replace(", ", "\n"));
        binding.gender.setText(gender);
        binding.major.setText(majorText);
        if (!addCourses.equals("")) {
            binding.addCourses.setText(addCourses);
        }
        if (!status.equals("")) {
            binding.status.setText(status);
        }
        if (!phone.equals("")) {
            binding.phone.setText(phone);
        }
        binding.address.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
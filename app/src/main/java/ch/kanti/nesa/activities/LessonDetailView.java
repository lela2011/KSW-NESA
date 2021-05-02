package ch.kanti.nesa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.ActivityLessonDetailViewBinding;

public class LessonDetailView extends AppCompatActivity {

    ActivityLessonDetailViewBinding binding;

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

        binding = ActivityLessonDetailViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String subject = intent.getStringExtra("subject");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String teacher = intent.getStringExtra("teacher");
        String room = intent.getStringExtra("room");
        String marking = intent.getStringExtra("marking");
        String comment = intent.getStringExtra("comment");

        binding.subject.setText(subject);
        binding.date.setText(date);
        if (!time.equals("00:00:00 - 00:00:00")) {
            binding.time.setText(time);
        } else {
            binding.time.setText(getText(R.string.fullDay));
        }
        if(!teacher.isEmpty()) {
            binding.teacher.setText(teacher);
        }
        if(room != null) {
            binding.room.setText(room);
        }
        if(!marking.isEmpty()) {
            binding.description.setText(marking);
        }
        if (comment != null) {
            binding.comment.setText(comment);
        }
    }
}
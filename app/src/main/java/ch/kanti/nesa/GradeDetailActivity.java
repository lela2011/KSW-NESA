package ch.kanti.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import ch.kanti.nesa.databinding.ActivityGradeDetailBinding;

public class GradeDetailActivity extends AppCompatActivity {
    public ActivityGradeDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
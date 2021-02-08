package ch.kanti.nesa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ch.kanti.nesa.databinding.ActivitySubjectSettingsBinding;

public class SubjectSettings extends AppCompatActivity {
    ActivitySubjectSettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int countPluspoints = intent.getIntExtra("pluspointsCount", 1);
        int countAverage = intent.getIntExtra("averageCount", 1);

        String subjectName = intent.getStringExtra("subjectName");
        String subjectId = intent.getStringExtra("subjectId");

        binding.subjectName.setText(subjectName);
        binding.subjectNameEdit.setText(subjectName);
        binding.addPluspointsCheck.setChecked(countPluspoints == 1);
        binding.averageCheck.setChecked(countAverage == 1);

        binding.applySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                String subjectNameNew = String.valueOf(binding.subjectNameEdit.getText());
                int countsPluspointsNew;
                int countsAverageNew;
                if (!subjectNameNew.matches("")) {
                    if (binding.addPluspointsCheck.isChecked()) {
                        countsPluspointsNew = 1;
                    } else {
                        countsPluspointsNew = 0;
                    }

                    if (binding.averageCheck.isChecked()) {
                        countsAverageNew = 1;
                    } else {
                        countsAverageNew = 0;
                    }

                    data.putExtra("subjectName", subjectNameNew);
                    data.putExtra("pluspointsCount", countsPluspointsNew);
                    data.putExtra("averageCounts", countsAverageNew);
                    data.putExtra("subjectId", subjectId);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(SubjectSettings.this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
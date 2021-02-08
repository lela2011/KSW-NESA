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
        int count = intent.getIntExtra("pluspointsCount", 1);
        String subjectName = intent.getStringExtra("subjectName");
        String subjectId = intent.getStringExtra("subjectId");

        binding.subjectName.setText(subjectName);
        binding.subjectNameEdit.setText(subjectName);
        binding.addPluspointsCheck.setChecked(count == 1);

        binding.applySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subjectNameNew = String.valueOf(binding.subjectNameEdit.getText());
                int countsNew;
                if (!subjectNameNew.matches("")) {
                    if (binding.addPluspointsCheck.isChecked()) {
                        countsNew = 1;
                    } else {
                        countsNew = 0;
                    }

                    Intent newIntent = new Intent();
                    newIntent.putExtra("subjectName", subjectNameNew);
                    newIntent.putExtra("pluspointsCount", countsNew);
                    newIntent.putExtra("subjectId", subjectId);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(SubjectSettings.this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
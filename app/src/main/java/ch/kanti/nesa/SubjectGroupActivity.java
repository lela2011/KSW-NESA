package ch.kanti.nesa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import javax.security.auth.Subject;

import ch.kanti.nesa.databinding.ActivitySubjectGroupBinding;
import ch.kanti.nesa.tables.Subjects;

public class SubjectGroupActivity extends AppCompatActivity {

    ActivitySubjectGroupBinding binding;
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ViewModel.class);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SubjectGroupAdapter adapter = new SubjectGroupAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getNonSetSubjectIds().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                adapter.setStatements(strings);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ids = "";
                String groupName = "";
                String weights = "";
                HashMap<String, Float> newGroupItems = adapter.getGroupItems();
                for (String i : newGroupItems.keySet()) {
                    ids = ids + i + "&";
                    weights = weights + newGroupItems.get(i) + "&";
                }
                groupName = binding.groupname.getText().toString();
                if (!groupName.isEmpty()) {
                    ids = ids.substring(0, ids.length() - 1);
                    weights = weights.substring(0, weights.length() -1);
                    Subjects subjectGroup = new Subjects(groupName, weights, -1f, -10f, ids, 0, 1, 1, 1, 0, 1);
                    viewModel.insertSubjectSet(subjectGroup);
                    finish();
                } else {
                    Toast.makeText(SubjectGroupActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
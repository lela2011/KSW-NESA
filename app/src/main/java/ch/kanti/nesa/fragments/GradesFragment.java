package ch.kanti.nesa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nesa.databinding.FragmentGradesBinding;

import java.util.List;

import ch.kanti.nesa.MainActivity;
import ch.kanti.nesa.SubjectAdapter;
import ch.kanti.nesa.tables.Subjects;

public class GradesFragment extends Fragment {

    public FragmentGradesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGradesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        RecyclerView recyclerView = binding.subjectRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        SubjectAdapter adapter = new SubjectAdapter();
        recyclerView.setAdapter(adapter);

        MainActivity.viewModel.getSubjects().observe(getActivity(), new Observer<List<Subjects>>() {
            @Override
            public void onChanged(List<Subjects> subjects) {
                adapter.setStatements(subjects);
            }
        });

        adapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subjects subject) {
                Toast.makeText(getActivity(), "Item " + subject.getSubjectName() + " was clicked", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemLongClickListener(new SubjectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Subjects subject) {
                Toast.makeText(getActivity(), "Item " + subject.getSubjectName() + " was long clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

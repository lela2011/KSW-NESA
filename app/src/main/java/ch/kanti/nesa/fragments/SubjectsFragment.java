package ch.kanti.nesa.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.R;
import ch.kanti.nesa.SubjectGroupActivity;
import ch.kanti.nesa.SubjectSettings;
import ch.kanti.nesa.databinding.FragmentSubjectsBinding;

import java.text.DecimalFormat;
import java.util.List;

import ch.kanti.nesa.SubjectAdapter;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.tables.Subjects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SubjectsFragment extends Fragment {

    public FragmentSubjectsBinding binding;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;

    public static RecyclerView recyclerView;
    public static SubjectAdapter subjectAdapter;

    public static final int SETTINGS_REQUEST_CODE = 1;

    int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
        if (getArguments() != null){
            position = getArguments().getInt("position", 0);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        recyclerView = binding.subjectRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        subjectAdapter = new SubjectAdapter();
        recyclerView.setAdapter(subjectAdapter);

        viewModel.getSubjects().observe(getViewLifecycleOwner(), new Observer<List<Subjects>>() {
            @Override
            public void onChanged(List<Subjects> subjects) {
                subjectAdapter.setStatements(subjects);
                recyclerView.scrollToPosition(position);
            }
        });

        binding.addGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SubjectGroupActivity.class);
                startActivity(intent);
            }
        });

        viewModel.getSubjectAverage().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.average.setText(df.format(aFloat));
                if (aFloat >= 5.0f) {
                    binding.average.setTextColor(ContextCompat.getColor(binding.average.getContext(), R.color.green));
                } else if (aFloat >= 4.0f) {
                    binding.average.setTextColor(ContextCompat.getColor(binding.average.getContext(), R.color.orange));
                } else {
                    binding.average.setTextColor(ContextCompat.getColor(binding.average.getContext(), R.color.red));
                }
            }
        });

        viewModel.getPluspoints().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.pluspoints.setText(df.format(aFloat));
                if (aFloat > 0) {
                    binding.pluspoints.setTextColor(ContextCompat.getColor(binding.pluspoints.getContext(), R.color.green));
                } else {
                    binding.pluspoints.setTextColor(ContextCompat.getColor(binding.pluspoints.getContext(), R.color.red));
                }
            }
        });

        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subjects subject, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("subject", subject.getId());
                bundle.putFloat("average", subject.getGradeAverage());
                bundle.putFloat("pluspoints", subject.getPluspoints());
                bundle.putInt("position", position);
                GradesFragment newSubject = new GradesFragment();
                newSubject.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSubject, "GRADES_FRAGMENT").commit();
            }
        });

        subjectAdapter.setOnItemLongClickListener(new SubjectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Subjects subject) {
                Intent intent = new Intent(getContext(), SubjectSettings.class);
                intent.putExtra("subjectName", subject.getSubjectName());
                intent.putExtra("pluspointsCount", subject.getCountsPluspoints());
                intent.putExtra("averageCount", subject.getCountsAverage());
                intent.putExtra("subjectId", subject.getId());
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            String subjectNameNew = data.getStringExtra("subjectName");
            int countsPluspointsNew = data.getIntExtra("pluspointsCount", 1);
            int countsAverageNew = data.getIntExtra("averageCounts", 1);
            String subjectId = data.getStringExtra("subjectId");

            viewModel.updateNameCountsSubject(subjectNameNew, countsPluspointsNew, countsAverageNew, subjectId);
        } else if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "Not saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

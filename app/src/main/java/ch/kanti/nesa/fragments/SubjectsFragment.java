package ch.kanti.nesa.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
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

import com.example.nesa.R;
import com.example.nesa.databinding.FragmentSubjectsBinding;

import java.text.DecimalFormat;
import java.util.List;

import ch.kanti.nesa.GradeAdapter;
import ch.kanti.nesa.MainActivity;
import ch.kanti.nesa.SubjectAdapter;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;

public class SubjectsFragment extends Fragment {

    public FragmentSubjectsBinding binding;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;

    public static RecyclerView recyclerView;
    public static SubjectAdapter subjectAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
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
            public void onItemClick(Subjects subject) {
                Bundle bundle = new Bundle();
                bundle.putString("subject", subject.getId());
                bundle.putFloat("average", subject.getGradeAverage());
                bundle.putFloat("pluspoints", subject.getPluspoints());
                GradesFragment newSubject = new GradesFragment();
                newSubject.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSubject, "GRADES_FRAGMENT").commit();
            }
        });

        subjectAdapter.setOnItemLongClickListener(new SubjectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Subjects subject) {
                Toast.makeText(getActivity(), "Item " + subject.getSubjectName() + " was long clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

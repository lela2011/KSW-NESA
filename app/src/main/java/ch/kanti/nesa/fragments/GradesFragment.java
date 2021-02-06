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
import com.example.nesa.databinding.FragmentGradesBinding;

import java.text.DecimalFormat;
import java.util.List;

import ch.kanti.nesa.GradeAdapter;
import ch.kanti.nesa.MainActivity;
import ch.kanti.nesa.SubjectAdapter;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;

public class GradesFragment extends Fragment {

    public FragmentGradesBinding binding;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;
    public static final int SUBJECT_VIEW = 1;
    public static final int GRADE_VIEW = 0;
    public static int currentAdapter;

    public static RecyclerView recyclerView;
    public static SubjectAdapter subjectAdapter;
    public GradeAdapter gradeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGradesBinding.inflate(inflater, container, false);
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
        gradeAdapter = new GradeAdapter();
        currentAdapter = SUBJECT_VIEW;

        viewModel.getSubjects().observe(getViewLifecycleOwner(), new Observer<List<Subjects>>() {
            @Override
            public void onChanged(List<Subjects> subjects) {
                recyclerView.setAdapter(subjectAdapter);
                subjectAdapter.setStatements(subjects);
                binding.backButton.setVisibility(View.GONE);
                currentAdapter = SUBJECT_VIEW;
            }
        });

        setAverageAndPluspointsOverview();

        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subjects subject) {
                viewModel.getGradeBySubject(subject.getId()).observe(getViewLifecycleOwner(), new Observer<List<Grades>>() {
                    @Override
                    public void onChanged(List<Grades> grades) {
                        if (currentAdapter == SUBJECT_VIEW) {
                            recyclerView.setAdapter(gradeAdapter);
                            currentAdapter = GRADE_VIEW;
                            gradeAdapter.setStatements(grades);
                            Float average = subject.getGradeAverage();
                            Float pluspoints = subject.getPluspoints();
                            binding.backButton.setVisibility(View.VISIBLE);
                            binding.average.setText(df.format(subject.getGradeAverage()));
                            binding.pluspoints.setText(df.format(subject.getPluspoints()));

                            if (average >= 5.0f) {
                                binding.average.setTextColor(ContextCompat.getColor(binding.average.getContext(), R.color.green));
                            } else if (average >= 4.0f) {
                                binding.average.setTextColor(ContextCompat.getColor(binding.average.getContext(), R.color.orange));
                            } else if (average == -1.0f) {
                                binding.average.setText("-");
                                TypedValue typedValue = new TypedValue();
                                Resources.Theme theme = binding.average.getContext().getTheme();
                                theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
                                binding.average.setTextColor(typedValue.data);
                            } else {
                                binding.average.setTextColor(ContextCompat.getColor(binding.average.getContext(), R.color.red));
                            }

                            if (pluspoints > 0) {
                                binding.pluspoints.setTextColor(ContextCompat.getColor(binding.pluspoints.getContext(), R.color.green));
                            } else if (pluspoints == -10.0f) {
                                binding.pluspoints.setText("-");
                                TypedValue typedValue = new TypedValue();
                                Resources.Theme theme = binding.pluspoints.getContext().getTheme();
                                theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
                                binding.pluspoints.setTextColor(typedValue.data);
                            } else {
                                binding.pluspoints.setTextColor(ContextCompat.getColor(binding.pluspoints.getContext(), R.color.red));
                            }
                        }
                    }
                });
            }
        });

        subjectAdapter.setOnItemLongClickListener(new SubjectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Subjects subject) {
                Toast.makeText(getActivity(), "Item " + subject.getSubjectName() + " was long clicked", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setAdapter(subjectAdapter);
                binding.backButton.setVisibility(View.GONE);
                currentAdapter = SUBJECT_VIEW;
                setAverageAndPluspointsOverview();
            }
        });
    }

    public void setAverageAndPluspointsOverview() {
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
    }
}

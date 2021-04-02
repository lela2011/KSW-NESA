package ch.kanti.nesa.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.GradeDetailActivity;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.FragmentGradesBinding;

import java.text.DecimalFormat;

import ch.kanti.nesa.adapters.GradeAdapter;
import ch.kanti.nesa.ViewModel;

public class GradesFragment extends Fragment {

    FragmentGradesBinding binding;
    ViewModel viewModel;
    RecyclerView recyclerView;
    GradeAdapter gradeAdapter;


    public static final DecimalFormat df = new DecimalFormat("#.###");

    String subjectId;
    Float average, pluspoints;
    int subjectPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGradesBinding.inflate(inflater, container, false);
        subjectId = getArguments().getString("subject");
        average = getArguments().getFloat("average");
        pluspoints = getArguments().getFloat("pluspoints");
        subjectPosition = getArguments().getInt("position");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        recyclerView = binding.subjectRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        gradeAdapter = new GradeAdapter();
        recyclerView.setAdapter(gradeAdapter);

        viewModel.getGradeBySubject(subjectId).observe(getViewLifecycleOwner(), grades -> {
            gradeAdapter.setStatements(grades);
            if (grades.size() == 0) {
                binding.empty.setVisibility(View.VISIBLE);
            } else {
                binding.empty.setVisibility(View.INVISIBLE);
            }
        });

        binding.average.setText(df.format(average));
        binding.pluspoints.setText(df.format(pluspoints));

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

        gradeAdapter.setOnItemClickListener(grade -> {
            Intent intent = new Intent(getContext(), GradeDetailActivity.class);
            intent.putExtra("date", grade.getDate());
            intent.putExtra("name", grade.getExam());
            intent.putExtra("weight", grade.getWeight());
            intent.putExtra("grade", grade.getGrade());

            startActivity(intent);
        });
    }

    public int getSubjectPosition() {
        return subjectPosition;
    }
}

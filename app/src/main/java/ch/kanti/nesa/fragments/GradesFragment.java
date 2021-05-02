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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.App;
import ch.kanti.nesa.activities.GradeDetailActivity;
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
    
    int col1, col2, col3, col4;
    float range3, range4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGradesBinding.inflate(inflater, container, false);
        subjectId = getArguments().getString("subject");
        average = getArguments().getFloat("average");
        pluspoints = getArguments().getFloat("pluspoints");
        subjectPosition = getArguments().getInt("position");

        col1 = App.sharedPreferences.getInt("colCol1",  getContext().getColor(R.color.gold));
        col2 = App.sharedPreferences.getInt("colCol2",  getContext().getColor(R.color.green));
        col3 = App.sharedPreferences.getInt("colCol3",  getContext().getColor(R.color.orange));
        col4 = App.sharedPreferences.getInt("colCol4",  getContext().getColor(R.color.red));
        range3 = App.sharedPreferences.getFloat("colRange1", 5f);
        range4 = App.sharedPreferences.getFloat("colRange2", 4f);
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
        
        if (average == 6.0f) {
            binding.average.setTextColor(col1);
        } else if (range3 > range4 && average >= range3) {
            binding.average.setTextColor(col2);
        } else if (range4 > range3 && average >= range4) {
            binding.average.setTextColor(col2);
        } else if (range3 > range4 && average >= range4) {
            binding.average.setTextColor(col3);
        } else if (range4 > range3 && average >= range3) {
            binding.average.setTextColor(col4);
        } else if (range3 > range4 && average < range4 && average != -1) {
            binding.average.setTextColor(col4);
        } else if (range4 > range3 && average < range3 && average != -1) {
            binding.average.setTextColor(col3);
        } else  {
            binding.average.setText("-");
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = binding.average.getContext().getTheme();
            theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
            binding.average.setTextColor(typedValue.data);
        }

        if (pluspoints >= 0) {
            binding.pluspoints.setTextColor(getContext().getColor(R.color.green));
        } else if (pluspoints == -10.0f) {
            binding.pluspoints.setText("-");
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = binding.pluspoints.getContext().getTheme();
            theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
            binding.pluspoints.setTextColor(typedValue.data);
        } else if (pluspoints < 0) {
            binding.pluspoints.setTextColor(getContext().getColor(R.color.red));
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

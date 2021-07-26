package ch.kanti.nesa.fragments;

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

import java.text.DecimalFormat;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.adapters.SubjectAdapter;
import ch.kanti.nesa.databinding.FragmentSubjectsBinding;
import ch.kanti.nesa.dialogs.SubjectNameDialog;
import ch.kanti.nesa.scrapers.ContentScrapers;

public class SubjectsFragment extends Fragment implements SubjectNameDialog.DialogListener {

    public FragmentSubjectsBinding binding;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;

    public static RecyclerView recyclerView;
    public SubjectAdapter subjectAdapter;

    int position;

    int col1, col2, col3, col4;
    float range3, range4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
        if (getArguments() != null){
            position = getArguments().getInt("position", 0);
        }
        col1 = App.sharedPreferences.getInt("colCol1",  requireContext().getColor(R.color.gold));
        col2 = App.sharedPreferences.getInt("colCol2",  requireContext().getColor(R.color.green));
        col3 = App.sharedPreferences.getInt("colCol3",  requireContext().getColor(R.color.orange));
        col4 = App.sharedPreferences.getInt("colCol4",  requireContext().getColor(R.color.red));
        range3 = App.sharedPreferences.getFloat("colRange1", 5f);
        range4 = App.sharedPreferences.getFloat("colRange2", 4f);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        recyclerView = binding.subjectRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        subjectAdapter = new SubjectAdapter();
        recyclerView.setAdapter(subjectAdapter);

        viewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            subjectAdapter.setStatements(subjects);
            recyclerView.scrollToPosition(position);
            float pluspoints = ContentScrapers.calculatePromotionPoints(subjects);
            binding.pluspoints.setText(df.format(pluspoints));

            if (pluspoints > 2) {
                binding.pluspoints.setTextColor(requireContext().getColor(R.color.green));
            } else if (pluspoints <= 2 && pluspoints >= 0) {
                binding.pluspoints.setTextColor(requireContext().getColor(R.color.orange));
            } else if (pluspoints < 0 && pluspoints != -10.0f) {
                binding.pluspoints.setTextColor(requireContext().getColor(R.color.red));
            } else if (pluspoints == -10.0f) {
                binding.pluspoints.setText("-");
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = binding.pluspoints.getContext().getTheme();
                theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
                binding.pluspoints.setTextColor(typedValue.data);
            }
        });

        viewModel.getSubjectAverage().observe(getViewLifecycleOwner(), aFloat -> {
            if (aFloat != null) {
                binding.average.setText(df.format(aFloat));
                if (aFloat == 6.0f) {
                    binding.average.setTextColor(col1);
                } else if (range3 > range4 && aFloat >= range3) {
                    binding.average.setTextColor(col2);
                } else if (range4 > range3 && aFloat >= range4) {
                    binding.average.setTextColor(col2);
                } else if (range3 > range4 && aFloat >= range4) {
                    binding.average.setTextColor(col3);
                } else if (range4 > range3 && aFloat >= range3) {
                    binding.average.setTextColor(col4);
                } else if (range3 > range4 && aFloat < range4 && aFloat != -1) {
                    binding.average.setTextColor(col4);
                } else if (range4 > range3 && aFloat < range3 && aFloat != -1) {
                    binding.average.setTextColor(col3);
                }
            } else {
                binding.average.setText("-");
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = requireContext().getTheme();
                theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
                binding.average.setTextColor(typedValue.data);
            }
        });

        subjectAdapter.setOnItemClickListener((subject, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString("subject", subject.getId());
            bundle.putFloat("average", subject.getGradeAverage());
            bundle.putFloat("pluspoints", subject.getPluspoints());
            bundle.putInt("position", position);
            GradesFragment newSubject = new GradesFragment();
            newSubject.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSubject, "GRADES_FRAGMENT").commit();
        });

        subjectAdapter.setOnItemLongClickListener(subject -> {
            SubjectNameDialog dialog = new SubjectNameDialog();
            Bundle bundle = new Bundle();
            bundle.putString("name", subject.getSubjectName());
            bundle.putString("id", subject.getId());
            dialog.setArguments(bundle);
            dialog.show(getChildFragmentManager(), "change subject name dialog");
        });
    }

    @Override
    public void applyText(String id, String name) {
        viewModel.updateNameSubject(id, name);
    }
}

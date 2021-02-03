package ch.kanti.nesa.fragments;

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

import com.example.nesa.R;
import com.example.nesa.databinding.FragmentGradesBinding;

import java.text.DecimalFormat;
import java.util.List;

import ch.kanti.nesa.MainActivity;
import ch.kanti.nesa.SubjectAdapter;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.tables.Subjects;

public class GradesFragment extends Fragment {

    public FragmentGradesBinding binding;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;

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

        RecyclerView recyclerView = binding.subjectRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        SubjectAdapter adapter = new SubjectAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getSubjects().observe(getViewLifecycleOwner(), new Observer<List<Subjects>>() {
            @Override
            public void onChanged(List<Subjects> subjects) {
                adapter.setStatements(subjects);
            }
        });

        viewModel.getSubjectAverage().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.average.setText(df.format(aFloat));
                if (aFloat >= 5.0f) {
                    binding.average.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                } else if (aFloat >= 4.0f) {
                    binding.average.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                } else {
                    binding.average.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
            }
        });

        viewModel.getPluspoints().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.pluspoints.setText(df.format(aFloat));
                if (aFloat > 0) {
                    binding.pluspoints.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                } else {
                    binding.pluspoints.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
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

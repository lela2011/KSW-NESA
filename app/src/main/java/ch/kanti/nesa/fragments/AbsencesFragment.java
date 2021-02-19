package ch.kanti.nesa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.kanti.nesa.AbsenceAdapter;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.FragmentAbsencesBinding;
import ch.kanti.nesa.tables.Absence;

public class AbsencesFragment extends Fragment {

    public FragmentAbsencesBinding binding;
    private ViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAbsencesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        RecyclerView recyclerView = binding.absencesRecview;

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        AbsenceAdapter adapter = new AbsenceAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getAbsences().observe(getViewLifecycleOwner(), new Observer<List<Absence>>() {
            @Override
            public void onChanged(List<Absence> absences) {
                adapter.setStatements(absences);
                if (absences.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.openAbsences.setText("-");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    binding.empty.setVisibility(View.GONE);
                    binding.openAbsences.setText(String.valueOf(absences.size()));
                }
            }
        });
    }
}

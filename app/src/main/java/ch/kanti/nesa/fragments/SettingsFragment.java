package ch.kanti.nesa.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    public FragmentSettingsBinding binding;
    public ViewModel viewModel;
    public Float grade1;
    public Float grade2;
    public Float grade3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        grade1 = Float.parseFloat(binding.editGradRange1.getText().toString());
        grade2 = Float.parseFloat(binding.editGradRange2.getText().toString());
        grade3 = Float.parseFloat(binding.editGradRange3.getText().toString());

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logout code
            }
        });

        binding.colorPickerBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.colorPickerBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.colorPickerBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.colorPickerBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}

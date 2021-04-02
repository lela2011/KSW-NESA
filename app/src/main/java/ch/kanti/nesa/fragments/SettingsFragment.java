package ch.kanti.nesa.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.App;
import ch.kanti.nesa.ColorPickerDialog;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.activities.SplashActivity;
import ch.kanti.nesa.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements ColorPickerDialog.ReturnColor {

    public FragmentSettingsBinding binding;
    public ViewModel viewModel;
    int col1, col2, col3, col4;

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

        col1 = App.sharedPreferences.getInt("colCol1",  getContext().getColor(R.color.gold));
        col2 = App.sharedPreferences.getInt("colCol2",  getContext().getColor(R.color.green));
        col3 = App.sharedPreferences.getInt("colCol3",  getContext().getColor(R.color.orange));
        col4 = App.sharedPreferences.getInt("colCol4",  getContext().getColor(R.color.red));

        float range1old = App.sharedPreferences.getFloat("colRange1", 5f);
        float range2old = App.sharedPreferences.getFloat("colRange2", 4f);

        binding.colorPickerBtn1.setBackgroundColor(col1);
        binding.colorPickerBtn2.setBackgroundColor(col2);
        binding.colorPickerBtn3.setBackgroundColor(col3);
        binding.colorPickerBtn4.setBackgroundColor(col4);

        binding.editGradRange2.setText(String.valueOf(range1old));
        binding.editGradRange3.setText(String.valueOf(range2old));

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logout code
            }
        });

        binding.colorPickerBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(0, col1);
            }
        });

        binding.colorPickerBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1, col2);
            }
        });

        binding.colorPickerBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(2, col3);
            }
        });

        binding.colorPickerBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(3, col4);
            }
        });

        binding.colorSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                String range1 = binding.editGradRange2.getText().toString();
                String range2 = binding.editGradRange3.getText().toString();
                if(!range1.isEmpty() || !range2.isEmpty()) {
                    float range1f = Float.parseFloat(range1);
                    float range2f = Float.parseFloat(range2);
                    if (range1f >= 1.0f && range1f <= 6.0f && range2f>= 1.0f && range2f <= 6.0f && range1f != range2f) {
                        App.sharedPreferences.edit().putInt("colCol1", col1).commit();
                        App.sharedPreferences.edit().putInt("colCol2", col2).commit();
                        App.sharedPreferences.edit().putInt("colCol3", col3).commit();
                        App.sharedPreferences.edit().putInt("colCol4", col4).commit();
                        App.sharedPreferences.edit().putFloat("colRange1", range1f).commit();
                        App.sharedPreferences.edit().putFloat("colRange2", range2f).commit();
                    } else {
                        Toast.makeText(getContext(), "Please enter real values", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter real values", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void returnColor(int range, int color) {
        switch (range) {
            case 0:
                binding.colorPickerBtn1.setBackgroundColor(color);
                col1 = color;
                break;
            case 1:
                binding.colorPickerBtn2.setBackgroundColor(color);
                col2 = color;
                break;
            case 2:
                binding.colorPickerBtn3.setBackgroundColor(color);
                col3 = color;
                break;
            case 3:
                binding.colorPickerBtn4.setBackgroundColor(color);
                col4 = color;
                break;
        }
    }

    private void showDialog(int range, int color) {
        ColorPickerDialog dialog = new ColorPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("range", range);
        bundle.putInt("oldColor", color);
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), "color picker");
    }
}

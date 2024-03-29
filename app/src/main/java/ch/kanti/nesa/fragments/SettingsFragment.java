package ch.kanti.nesa.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.kanti.nesa.App;
import ch.kanti.nesa.activities.SplashActivity;
import ch.kanti.nesa.dialogs.ColorPickerDialog;
import ch.kanti.nesa.R;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.activities.LoginActivity;
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

    @SuppressLint("ApplySharedPref")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(getContext(), R.array.themespinner, android.R.layout.simple_spinner_item);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterVariant = ArrayAdapter.createFromResource(getContext(), R.array.screentype, android.R.layout.simple_spinner_item);
        adapterVariant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.themeSpinner.setAdapter(adapterTheme);
        binding.darkVariant.setAdapter(adapterVariant);

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

        binding.logout.setOnClickListener(view1 -> {
            // logout code
        });

        binding.colorPickerBtn1.setOnClickListener(view15 -> showDialog(0, col1));

        binding.colorPickerBtn2.setOnClickListener(view14 -> showDialog(1, col2));

        binding.colorPickerBtn3.setOnClickListener(view13 -> showDialog(2, col3));

        binding.colorPickerBtn4.setOnClickListener(view12 -> showDialog(3, col4));

        binding.colorSave.setOnClickListener(v -> {
            String range1 = binding.editGradRange2.getText().toString();
            String range2 = binding.editGradRange3.getText().toString();
            if(!range1.isEmpty() || !range2.isEmpty()) {
                float range1f = Float.parseFloat(range1);
                float range2f = Float.parseFloat(range2);
                if (range1f >= 1.0f && range1f <= 6.0f && range2f>= 1.0f && range2f <= 6.0f && range1f != range2f) {
                    App.sharedPreferences.edit().putInt("colCol1", col1).apply();
                    App.sharedPreferences.edit().putInt("colCol2", col2).apply();
                    App.sharedPreferences.edit().putInt("colCol3", col3).apply();
                    App.sharedPreferences.edit().putInt("colCol4", col4).apply();
                    App.sharedPreferences.edit().putFloat("colRange1", range1f).apply();
                    App.sharedPreferences.edit().putFloat("colRange2", range2f).apply();
                } else {
                    Toast.makeText(getContext(), getText(R.string.invalidValues), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getText(R.string.invalidValues), Toast.LENGTH_SHORT).show();
            }

        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(()->{
                    App.sharedPreferences.edit().putString("username", "").apply();
                    App.sharedPreferences.edit().putString("password", "").apply();
                    App.sharedPreferences.edit().putBoolean(App.LOGIN_COMPLETED, false).apply();
                    App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, true).apply();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    viewModel.deleteAllBank();
                    viewModel.deleteAllAbsences();
                    viewModel.deleteAllSubjects();
                    viewModel.deleteAllGrades();
                    viewModel.deleteAllAccountInfo();
                    viewModel.deleteAllStudents();
                    viewModel.deleteAllLessons();
                });
            }
        });

        int theme = App.sharedPreferences.getInt("theme", 0);
        int border = App.sharedPreferences.getInt("border", 0);

        binding.themeSpinner.setSelection(theme);
        binding.darkVariant.setSelection(border);

        binding.themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.sharedPreferences.edit().putInt("theme", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.darkVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.sharedPreferences.edit().putInt("border", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.saveTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().recreate();
            }
        });

        binding.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.sharedPreferences.edit().putBoolean(App.LOGIN_COMPLETED, true).apply();
                App.sharedPreferences.edit().putBoolean(App.FIRST_LOGIN, true).apply();
                Intent intent = new Intent(getContext(), SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
                viewModel.deleteAllBank();
                viewModel.deleteAllAbsences();
                viewModel.deleteAllSubjects();
                viewModel.deleteAllGrades();
                //viewModel.deleteAllAccountInfo();
                viewModel.deleteAllStudents();
                viewModel.deleteAllLessons();
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

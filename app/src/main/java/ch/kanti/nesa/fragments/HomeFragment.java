package ch.kanti.nesa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ch.kanti.nesa.activities.MainActivity;
import ch.kanti.nesa.ViewModel;

import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.FragmentHomeBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_ABSENCE;
import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_BANK;
import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_GRADES;

public class HomeFragment extends Fragment {

    public FragmentHomeBinding binding;
    private HomeFragmentShortcut shortcut;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;

    public interface HomeFragmentShortcut {
        void onShortcutClicked(int shortcut);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        ArrayList<TextView> personalInfoTextViews = new ArrayList<>(8);
        personalInfoTextViews.add(binding.name);
        personalInfoTextViews.add(binding.address);
        personalInfoTextViews.add(binding.city);
        personalInfoTextViews.add(binding.birthdate);
        personalInfoTextViews.add(binding.major);
        personalInfoTextViews.add(binding.familyorigin);
        personalInfoTextViews.add(binding.phone);
        personalInfoTextViews.add(binding.mobilephone);
        personalInfoTextViews.add(binding.schoolMail);
        personalInfoTextViews.add(binding.privatemail);

        MainActivity.viewModel.getAccountInfo().observe(getViewLifecycleOwner(), accountInfos -> {
            for(int i = 0; i < accountInfos.size(); i++){
                personalInfoTextViews.get(i).setText(accountInfos.get(i).value);
            }
        });

        viewModel.getBalance().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat != null){
                String balance = aFloat + " " + getString(R.string.chf);
                binding.balance.setText(balance);
                if(aFloat >= 100){
                    binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                } else if(aFloat > 0) {
                    binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                } else {
                    binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
            }
        });

        viewModel.getSubjectAverage().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.gradeAverage.setText(String.valueOf(df.format(aFloat)));
                if(aFloat != null) {
                    if (aFloat >= 5.0f) {
                        binding.gradeAverage.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                    } else if (aFloat >= 4.0f) {
                        binding.gradeAverage.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                    } else {
                        binding.gradeAverage.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    }
                }
            }
        });

        viewModel.getPluspoints().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.pluspoints.setText(String.valueOf(df.format(aFloat)));
                if (aFloat != null) {
                    if (aFloat > 0) {
                        binding.pluspoints.setTextColor(ContextCompat.getColor(getContext() , R.color.green));
                    } else {
                        binding.pluspoints.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    }
                }
            }
        });

        viewModel.getAbsenceSize().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    binding.openAbsencesHome.setText("-");
                } else {
                    binding.openAbsencesHome.setText(String.valueOf(integer));
                }
            }
        });

        binding.bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortcut.onShortcutClicked(SHORTCUT_BANK);
            }
        });

        binding.marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortcut.onShortcutClicked(SHORTCUT_GRADES);
            }
        });

        binding.openAbsencesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shortcut.onShortcutClicked(SHORTCUT_ABSENCE);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeFragmentShortcut) {
            shortcut = (HomeFragmentShortcut) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement HomeFragmentShortcut");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shortcut = null;
    }
}

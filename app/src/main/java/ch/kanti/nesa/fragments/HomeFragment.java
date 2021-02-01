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

import ch.kanti.nesa.MainActivity;
import com.example.nesa.R;
import com.example.nesa.databinding.FragmentHomeBinding;

import java.util.ArrayList;

import static ch.kanti.nesa.MainActivity.SHORTCUT_BANK;
import static ch.kanti.nesa.MainActivity.SHORTCUT_GRADES;

public class HomeFragment extends Fragment {

    public FragmentHomeBinding binding;
    private HomeFragmentShortcut shortcut;

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
    public void onStart() {
        super.onStart();

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

        MainActivity.viewModel.getAccountInfo().observe(getActivity(), accountInfos -> {
            for(int i = 0; i < accountInfos.size(); i++){
                    personalInfoTextViews.get(i).setText(accountInfos.get(i).value);
            }
        });

        MainActivity.viewModel.getBalance().observe(getActivity(), aFloat -> {
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

        MainActivity.viewModel.getSubjectAverage().observe(getActivity(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.gradeAverage.setText(String.valueOf(aFloat));
                /*if(aFloat != null) {
                    if (aFloat >= 5.0f) {
                        binding.gradeAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                    } else if (aFloat >= 4.0f) {
                        binding.gradeAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.orange));
                    } else {
                        binding.gradeAverage.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    }
                }*/
            }
        });

        MainActivity.viewModel.getPluspoints().observe(getActivity(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.pluspoints.setText(String.valueOf(aFloat));
                /*if (aFloat != null) {
                    if (aFloat > 0) {
                        binding.pluspoints.setTextColor(ContextCompat.getColor(getActivity() , R.color.green));
                    } else {
                        binding.pluspoints.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    }
                }*/
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

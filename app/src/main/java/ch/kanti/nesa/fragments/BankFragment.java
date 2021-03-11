package ch.kanti.nesa.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import ch.kanti.nesa.BankAdapter;
import ch.kanti.nesa.DetailedBankStatement;
import ch.kanti.nesa.ViewModel;

import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.FragmentAccountBinding;

public class BankFragment extends Fragment {

    public FragmentAccountBinding binding;
    public ViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        BankAdapter adapter = new BankAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getBankStatements().observe(getViewLifecycleOwner(), statements -> {
            adapter.setStatements(statements);
            float balanceFloat = statements.get(statements.size()-1).getBalance();
            @SuppressLint("DefaultLocale") String balance = String.format("%.2f",balanceFloat);
            binding.balance.setText(balance);
            if(balanceFloat >= 100){
                binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            } else if(balanceFloat > 0) {
                binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
            } else {
                binding.balance.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            }
        });

        adapter.setOnItemClickListener(statement -> {
            Intent intent = new Intent(getActivity(), DetailedBankStatement.class);
            intent.putExtra("Date", statement.getDate());
            intent.putExtra("Description", statement.getTitle());
            intent.putExtra("Amount", statement.getAmount());
            intent.putExtra("Balance", statement.getBalance());
            startActivity(intent);
        });
    }
}

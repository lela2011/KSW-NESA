package com.example.nesa;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nesa.databinding.BankRecyclerItemBinding;
import com.example.nesa.tables.BankStatement;

import java.util.ArrayList;
import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankStatementHolder> {

    private List<BankStatement> statements = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public BankStatementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BankRecyclerItemBinding binding = BankRecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BankStatementHolder(binding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull BankStatementHolder holder, int position) {
        BankStatement currentStatement = statements.get(position);
        holder.dateView.setText(currentStatement.date);
        holder.titleView.setText(currentStatement.title);
        holder.amountView.setText(String.format("%.2f", currentStatement.amount));
        if(currentStatement.amount > 0){
            holder.amountView.setTextColor(ContextCompat.getColor(holder.amountView.getContext(), R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return statements.size();
    }

    public void setStatements(List<BankStatement> statements){
        this.statements = statements;
        notifyDataSetChanged();
    }

    class BankStatementHolder extends RecyclerView.ViewHolder{

        private final TextView dateView;
        private final TextView titleView;
        private final TextView amountView;

        public BankStatementHolder(@NonNull BankRecyclerItemBinding binding) {
            super(binding.getRoot());
            dateView = binding.date;
            titleView = binding.title;
            amountView = binding.amount;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(statements.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BankStatement statement);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

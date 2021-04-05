package ch.kanti.nesa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.kanti.nesa.databinding.RecviewStudentsBinding;
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Student;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> dataList = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewStudentsBinding binding = RecviewStudentsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student currentItem = dataList.get(position);
        holder.name.setText(currentItem.getName());
        //Fill views with content
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Student> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //define views
        TextView name;
        public ViewHolder(@NonNull RecviewStudentsBinding binding) {
            super(binding.getRoot());
            name = binding.name;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(dataList.get(position));
                    }
                }
            });
            //bind views
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public void setOnItemClickListener(StudentAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}

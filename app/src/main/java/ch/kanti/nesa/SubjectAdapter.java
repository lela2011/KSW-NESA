package ch.kanti.nesa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nesa.R;
import com.example.nesa.databinding.RecviewSubjectBinding;

import ch.kanti.nesa.tables.Subjects;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subjects> dataList = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewSubjectBinding binding = RecviewSubjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subjects currentItem = dataList.get(position);
        Context context = holder.subjectAverage.getContext();
        String gradeAverage = "";
        Float gradeAverageFloat = currentItem.getGradeAverage();
        if (currentItem.getGradeAverage() == -1.0f) {
            gradeAverage = "-";
        } else {
            gradeAverage = String.valueOf(gradeAverageFloat);
        }
        holder.subjectName.setText(currentItem.getSubjectName());
        holder.subjectAverage.setText(gradeAverage);
        if (gradeAverageFloat >= 5.0f) {
            holder.subjectAverage.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
        else if (gradeAverageFloat < 5.0f && gradeAverageFloat >= 4.0f) {
            holder.subjectAverage.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else if (gradeAverageFloat < 4.0f && gradeAverageFloat >= 1.0f) {
            holder.subjectAverage.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.subjectAverage.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Subjects> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectName, subjectAverage;
        public SubjectViewHolder(@NonNull RecviewSubjectBinding binding) {
            super(binding.getRoot());
            subjectName = binding.subjectName;
            subjectAverage = binding.subjectAverage;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(dataList.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Subjects subject);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
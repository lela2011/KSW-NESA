package ch.kanti.nesa;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.databinding.RecviewGradeBinding;

import ch.kanti.nesa.tables.Grades;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private List<Grades> dataList = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat("#.###");

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewGradeBinding binding = RecviewGradeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GradeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        Grades currentItem = dataList.get(position);
        Context context = holder.grade.getContext();
        String gradeString = "";
        Float grade = currentItem.getGrade();
        if (currentItem.getGrade() == -1.0f) {
            gradeString = "-";
        } else {
            gradeString = String.valueOf(df.format(grade));
        }
        holder.grade.setText(gradeString);
        holder.gradeName.setText(currentItem.getExam());
        if (grade >= 6.0f) {
            holder.grade.setTextColor(ContextCompat.getColor(context, R.color.gold));
        } else if (grade >= 4.5f) {
            holder.grade.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (grade >= 3.75f) {
            holder.grade.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else if (grade >= 1.0f) {
            holder.grade.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
            holder.grade.setTextColor(typedValue.data);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Grades> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    class GradeViewHolder extends RecyclerView.ViewHolder {
        private final TextView gradeName;
        private final TextView grade;
        public GradeViewHolder(@NonNull RecviewGradeBinding binding) {
            super(binding.getRoot());
            gradeName = binding.gradeName;
            grade = binding.grade;
        }
    }
}

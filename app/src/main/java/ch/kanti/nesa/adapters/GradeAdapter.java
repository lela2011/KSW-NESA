package ch.kanti.nesa.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.RecviewGradeBinding;

import ch.kanti.nesa.tables.Grades;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private List<Grades> dataList = new ArrayList<>();
    private static final DecimalFormat df = new DecimalFormat("#.###");
    private OnItemClickListener listener;
    Context colorContext;
    int col1;
    int col2;
    int col3;
    int col4;

    float range3;
    float range4;

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewGradeBinding binding = RecviewGradeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        colorContext = parent.getContext();
        col1 = App.sharedPreferences.getInt("colCol1",  colorContext.getColor(R.color.gold));
        col2 = App.sharedPreferences.getInt("colCol2",  colorContext.getColor(R.color.green));
        col3 = App.sharedPreferences.getInt("colCol3",  colorContext.getColor(R.color.orange));
        col4 = App.sharedPreferences.getInt("colCol4",  colorContext.getColor(R.color.red));
        range3 = App.sharedPreferences.getFloat("colRange1", 5f);
        range4 = App.sharedPreferences.getFloat("colRange2", 4f);

        return new GradeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        Grades currentItem = dataList.get(position);
        Context context = holder.grade.getContext();
        String gradeString = "";
        float grade = currentItem.getGrade();
        if (currentItem.getGrade() == -1.0f) {
            gradeString = "-";
        } else {
            gradeString = String.valueOf(df.format(grade));
        }
        holder.grade.setText(gradeString);
        holder.gradeName.setText(currentItem.getExam());
        if (grade == 6.0f) {
            holder.grade.setTextColor(col1);
        } else if (range3 > range4 && grade >= range3) {
            holder.grade.setTextColor(col2);
        } else if (range4 > range3 && grade >= range4) {
            holder.grade.setTextColor(col2);
        } else if (range3 > range4 && grade >= range4) {
            holder.grade.setTextColor(col3);
        } else if (range4 > range3 && grade >= range3) {
            holder.grade.setTextColor(col4);
        } else if (range3 > range4 && grade < range4 && grade != -1) {
            holder.grade.setTextColor(col4);
        } else if (range4 > range3 && grade < range3 && grade != -1) {
            holder.grade.setTextColor(col3);
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

    public interface OnItemClickListener {
        void onItemClick(Grades grade);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

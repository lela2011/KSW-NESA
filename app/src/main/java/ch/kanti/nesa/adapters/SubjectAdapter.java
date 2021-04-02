package ch.kanti.nesa.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.App;
import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.RecviewSubjectBinding;

import ch.kanti.nesa.tables.Subject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> dataList = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private static final DecimalFormat df = new DecimalFormat("#.###");
    Context colorContext;
    int col1;
    int col2;
    int col3;
    int col4;

    float range3;
    float range4;

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewSubjectBinding binding = RecviewSubjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        colorContext = parent.getContext();
        col1 = App.sharedPreferences.getInt("colCol1",  colorContext.getColor(R.color.gold));
        col2 = App.sharedPreferences.getInt("colCol2",  colorContext.getColor(R.color.green));
        col3 = App.sharedPreferences.getInt("colCol3",  colorContext.getColor(R.color.orange));
        col4 = App.sharedPreferences.getInt("colCol4",  colorContext.getColor(R.color.red));
        range3 = App.sharedPreferences.getFloat("colRange1", 5f);
        range4 = App.sharedPreferences.getFloat("colRange2", 4f);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject currentItem = dataList.get(position);
        Context context = holder.subjectAverage.getContext();
        String gradeAverage;
        Float gradeAverageFloat = currentItem.getGradeAverage();
        if (currentItem.getGradeAverage() == -1.0f) {
            gradeAverage = "-";
        } else {
            gradeAverage = String.valueOf(df.format(gradeAverageFloat));
        }
        holder.subjectName.setText(currentItem.getSubjectName());
        holder.subjectId.setText(currentItem.getId());
        holder.subjectAverage.setText(gradeAverage);
        if (gradeAverageFloat == 6.0f) {
            holder.subjectAverage.setTextColor(col1);
        } else if (range3 > range4 && gradeAverageFloat >= range3) {
            holder.subjectAverage.setTextColor(col2);
        } else if (range4 > range3 && gradeAverageFloat >= range4) {
            holder.subjectAverage.setTextColor(col2);
        } else if (range3 > range4 && gradeAverageFloat >= range4) {
            holder.subjectAverage.setTextColor(col3);
        } else if (range4 > range3 && gradeAverageFloat >= range3) {
            holder.subjectAverage.setTextColor(col4);
        } else if (range3 > range4 && gradeAverageFloat < range4 && gradeAverageFloat != -1) {
            holder.subjectAverage.setTextColor(col4);
        } else if (range4 > range3 && gradeAverageFloat < range3 && gradeAverageFloat != -1) {
            holder.subjectAverage.setTextColor(col3);
        } else {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
            holder.subjectAverage.setTextColor(typedValue.data);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Subject> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectName, subjectId, subjectAverage;
        public SubjectViewHolder(@NonNull RecviewSubjectBinding binding) {
            super(binding.getRoot());
            subjectName = binding.subjectName;
            subjectId = binding.subjectId;
            subjectAverage = binding.subjectAverage;
            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(dataList.get(position), position);
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if(longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(dataList.get(position));
                }
                return true;
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Subject subject, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(Subject subject);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.longClickListener = listener;
    }
}
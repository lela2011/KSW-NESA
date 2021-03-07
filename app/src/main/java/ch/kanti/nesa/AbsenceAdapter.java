package ch.kanti.nesa;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.databinding.RecviewAbsenceBinding;
import ch.kanti.nesa.tables.Absence;

import java.util.ArrayList;
import java.util.List;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.AbsenceViewHolder> {

    private List<Absence> dataList = new ArrayList<>();

    @NonNull
    @Override
    public AbsenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewAbsenceBinding binding = RecviewAbsenceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AbsenceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceViewHolder holder, int position) {
        Absence currentItem = dataList.get(position);
        holder.date.setText(currentItem.getDate());
        holder.time.setText(currentItem.getTime());
        holder.course.setText(currentItem.getCourse());
        if (currentItem.getType() == 0) {
            holder.type.setText(R.string.absenceType);
        } else {
            holder.type.setText(R.string.delayType);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Absence> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    static class AbsenceViewHolder extends RecyclerView.ViewHolder {
        private final TextView date, time, course, type;
        //define views
        public AbsenceViewHolder(@NonNull RecviewAbsenceBinding binding) {
            super(binding.getRoot());
            date = binding.absenceDate;
            time = binding.absenceTime;
            course = binding.absenceCourse;
            type = binding.absenceType;
        }
    }
}

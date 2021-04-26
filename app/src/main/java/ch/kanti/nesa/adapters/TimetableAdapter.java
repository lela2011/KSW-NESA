package ch.kanti.nesa.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import ch.kanti.nesa.databinding.RecviewTimetableDayBinding;
import ch.kanti.nesa.tables.Lesson;

import java.util.ArrayList;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private List<Lesson> dataList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecviewTimetableDayBinding binding = RecviewTimetableDayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson currentItem = dataList.get(position);
        if(currentItem.getStartTime().equals("00:00:00")) {
            holder.time.setVisibility(View.GONE);
            holder.subject.setText(String.format("%s", currentItem.getSubject()));
        } else {
            holder.time.setText(String.format("%s - %s", currentItem.getStartTime(), currentItem.getEndTime()));
            holder.subject.setText(String.format("%s - %s", currentItem.getSubject(), currentItem.getRoom()));
        }
        holder.rootView.setCardBackgroundColor(Color.parseColor(currentItem.getColor()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Lesson> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        //define views
        TextView time, subject;
        MaterialCardView rootView;
        public ViewHolder(@NonNull RecviewTimetableDayBinding binding) {
            super(binding.getRoot());
            //bind views
            time = binding.time;
            subject = binding.subject;
            rootView = binding.rootView;
        }
    }
}

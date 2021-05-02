package ch.kanti.nesa.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.RecviewTimetableDayBinding;
import ch.kanti.nesa.tables.Lesson;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private List<Lesson> dataList = new ArrayList<>();
    private OnItemClickListener listener;

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
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(String.format("%s - %s", currentItem.getStartTime(), currentItem.getEndTime()));
            holder.subject.setText(String.format("%s - %s", currentItem.getSubject(), currentItem.getRoom()));
        }
        if(currentItem.isExam() || !currentItem.getMarking().equals("Keine Markierung")) {
            holder.rootView.setCardBackgroundColor(Color.parseColor(currentItem.getColor()));
            holder.time.setTextColor(Color.BLACK);
            holder.subject.setTextColor(Color.BLACK);
        } else {
            TypedValue typedValueCard = new TypedValue();
            Resources.Theme theme = holder.rootView.getContext().getTheme();
            theme.resolveAttribute(R.attr.colorSurface, typedValueCard, true);
            int color = holder.rootView.getContext().getColor(R.color.primaryTextColor);
            holder.time.setTextColor(color);
            holder.subject.setTextColor(color);
            holder.rootView.setCardBackgroundColor(typedValueCard.data);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<Lesson> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //define views
        TextView time, subject;
        MaterialCardView rootView;
        public ViewHolder(@NonNull RecviewTimetableDayBinding binding) {
            super(binding.getRoot());
            //bind views
            time = binding.time;
            subject = binding.subject;
            rootView = binding.rootView;
            rootView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataList.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }
}

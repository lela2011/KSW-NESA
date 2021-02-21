package ch.kanti.nesa;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.databinding.SubjectIdItemBinding;

import java.util.ArrayList;
import java.util.List;

public class SubjectGroupAdapter extends RecyclerView.Adapter<SubjectGroupAdapter.SubjectGroupHolder> {

    private List<String> dataList = new ArrayList<>();

    @NonNull
    @Override
    public SubjectGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SubjectIdItemBinding binding = SubjectIdItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SubjectGroupHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectGroupHolder holder, int position) {
        String currentItem = dataList.get(position);
        //Fill views with content
        holder.checkBox.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setStatements(List<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    class SubjectGroupHolder extends RecyclerView.ViewHolder {
        //define views
        private final CheckBox checkBox;
        private final EditText weight;
        public SubjectGroupHolder(@NonNull SubjectIdItemBinding binding) {
            super(binding.getRoot());
            //bind views
            checkBox = binding.checkbox;
            weight = binding.weight;
        }
    }
}

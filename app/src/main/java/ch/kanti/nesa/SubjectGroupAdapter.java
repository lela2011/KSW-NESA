package ch.kanti.nesa;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.databinding.SubjectIdItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubjectGroupAdapter extends RecyclerView.Adapter<SubjectGroupAdapter.SubjectGroupHolder> {

    private List<String> dataList = new ArrayList<>();
    private HashMap<String, Float> groupItems = new HashMap<>();

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
        holder.text.setText(currentItem);

        holder.weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // not needed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String groupItemId = String.valueOf(holder.text.getText());
                String groupItemWeightString = holder.weight.getText().toString();
                Float groupItemWeight = 0f;
                if (groupItemWeightString.isEmpty()) {
                    groupItemWeight = 0f;
                } else {
                    groupItemWeight = Float.parseFloat(groupItemWeightString);
                }
                if (groupItems.containsKey(groupItemId) && groupItemWeight != 0) {
                    groupItems.replace(groupItemId, groupItemWeight);
                } else if (groupItems.containsKey(groupItemId) && groupItemWeight == 0) {
                    groupItems.remove(groupItemId);
                } else {
                    groupItems.put(groupItemId, groupItemWeight);
                }
            }
        });
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
        private final TextView text;
        private final EditText weight;

        public SubjectGroupHolder(@NonNull SubjectIdItemBinding binding) {
            super(binding.getRoot());
            //bind views
            text = binding.text;
            weight = binding.weight;
        }
    }

    public HashMap<String, Float> getGroupItems() {
        return groupItems;
    }
}

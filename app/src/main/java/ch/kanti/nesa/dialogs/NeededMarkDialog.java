package ch.kanti.nesa.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ch.kanti.nesa.R;
import ch.kanti.nesa.databinding.NeededGradeDialogBinding;
import ch.kanti.nesa.tables.Grade;

public class NeededMarkDialog extends AppCompatDialogFragment {

    private List<Grade> grades;

    NeededGradeDialogBinding binding;

    public NeededMarkDialog(List<Grade> grades) {
        this.grades = grades;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.needed_grade_dialog, null);

        TextView t2_75 = view.findViewById(R.id.grade2_75);
        TextView t3_25 = view.findViewById(R.id.grade3_25);
        TextView t3_75 = view.findViewById(R.id.grade3_75);
        TextView t4_25 = view.findViewById(R.id.grade4_25);
        TextView t4_75 = view.findViewById(R.id.grade4_75);
        TextView t5_25 = view.findViewById(R.id.grade5_25);
        TextView t5_75 = view.findViewById(R.id.grade5_75);

        List<String> neededGrades = getNeededGrades(grades, 1);
        t2_75.setText(neededGrades.get(0));
        t3_25.setText(neededGrades.get(1));
        t3_75.setText(neededGrades.get(2));
        t4_25.setText(neededGrades.get(3));
        t4_75.setText(neededGrades.get(4));
        t5_25.setText(neededGrades.get(5));
        t5_75.setText(neededGrades.get(6));

        view.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = view.findViewById(R.id.weight);
                Float weight = Float.parseFloat(input.getText().toString());
                if (weight != 0) {
                    List<String> neededGrades = getNeededGrades(grades, weight);
                    t2_75.setText(neededGrades.get(0));
                    t3_25.setText(neededGrades.get(1));
                    t3_75.setText(neededGrades.get(2));
                    t4_25.setText(neededGrades.get(3));
                    t4_75.setText(neededGrades.get(4));
                    t5_25.setText(neededGrades.get(5));
                    t5_75.setText(neededGrades.get(6));
                }
            }
        });

        return builder.setView(view).create();
    }

    private List<String> getNeededGrades(List<Grade> grades, float weight) {

        DecimalFormat df = new DecimalFormat("#.000");

        List<Float> averages = new ArrayList<>();
        averages.add(2.75f);
        averages.add(3.25f);
        averages.add(3.75f);
        averages.add(4.25f);
        averages.add(4.75f);
        averages.add(5.25f);
        averages.add(5.75f);

        List<String> neededGrades = new ArrayList<>();

        float gradesSum = (float) grades.stream()
                .filter(r -> r.getGrade() != -1)
                .mapToDouble(r -> r.getWeight()*r.getGrade())
                .sum();

        float weightsSum = (float) grades.stream()
                .filter(r -> r.getGrade() != -1)
                .mapToDouble(Grade::getWeight)
                .sum();

        for(Float average : averages) {
            float neededGrade = (average * (weightsSum + weight) - gradesSum) / weight;
            if (neededGrade >= 1) {
                neededGrades.add(df.format(neededGrade));
            } else {
                neededGrades.add("-");
            }
        }

        return neededGrades;
    }
}

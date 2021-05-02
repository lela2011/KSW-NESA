package ch.kanti.nesa.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import ch.kanti.nesa.R;

public class SubjectNameDialog extends AppCompatDialogFragment {

    private EditText subjectName;
    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.subject_name_dialog, null);

        subjectName = view.findViewById(R.id.subNameModal);

        String name =  getArguments().getString("name");
        String subjectId = getArguments().getString("id");

        subjectName.setText(name);

        builder.setView(view)
                .setTitle(subjectId)
                .setNegativeButton(getString(R.string.dialogButtonCancel), (dialog, which) -> {

                })
                .setPositiveButton(getString(R.string.dialogButtonOk), (dialog, which) -> {
                    String newName = subjectName.getText().toString();
                    listener.applyText(subjectId, newName);
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogListener");
        }
    }

    public interface DialogListener {
        void applyText(String id, String name);
    }
}

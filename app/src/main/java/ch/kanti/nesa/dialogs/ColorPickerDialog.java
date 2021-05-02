package ch.kanti.nesa.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.regex.Pattern;

import ch.kanti.nesa.R;

public class ColorPickerDialog extends AppCompatDialogFragment {

    SeekBar sliderRed, sliderGreen, sliderBlue;
    EditText hex;
    View colorField;
    int color;

    final Pattern pattern = Pattern.compile("^[#0-9a-fA-F]+$");

    boolean isDragged = false;
    boolean isTyped = false;

    private ReturnColor listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.color_picker_dialog, null);

        sliderRed = view.findViewById(R.id.red_slider);
        sliderGreen = view.findViewById(R.id.green_slider);
        sliderBlue = view.findViewById(R.id.blue_slider);

        hex = view.findViewById(R.id.hex);

        hex.addTextChangedListener(watcher);

        colorField = view.findViewById(R.id.color_view);

        sliderRed.setOnSeekBarChangeListener(changeListener);
        sliderGreen.setOnSeekBarChangeListener(changeListener);
        sliderBlue.setOnSeekBarChangeListener(changeListener);

        Bundle bundle = getArguments();
        int range = bundle.getInt("range");
        String oldColor = String.format("#%06X",(0xFFFFFF & bundle.getInt("oldColor"))).replace("#","");
        int oldR = Math.round(Integer.parseInt(oldColor.substring(0,2),16)/2.55f);
        int oldG = Math.round(Integer.parseInt(oldColor.substring(2,4),16)/2.55f);
        int oldB = Math.round(Integer.parseInt(oldColor.substring(4,6),16)/2.55f);

        sliderRed.setProgress(oldR);
        sliderGreen.setProgress(oldG);
        sliderBlue.setProgress(oldB);
        colorField.setBackgroundColor(bundle.getInt("oldColor"));

        builder.setView(view)
                .setTitle("Pick color")
                .setPositiveButton(getString(R.string.dialogButtonOk), (dialog, which) -> listener.returnColor(range, color))
                .setNegativeButton(getString(R.string.dialogButtonCancel), (dialog, which) -> {

                });
        return builder.create();
    }

    final SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!isTyped) {
                int r = Math.round(sliderRed.getProgress()  * 2.55f);
                int g = Math.round(sliderGreen.getProgress() * 2.55f);
                int b = Math.round(sliderBlue.getProgress() * 2.55f);

                String rHex = Integer.toHexString(r);
                if (rHex.length() == 1) {
                    rHex = "0" + rHex;
                }
                String gHex = Integer.toHexString(g);
                if (gHex.length() == 1) {
                    gHex = "0" + gHex;
                }
                String bHex = Integer.toHexString(b);
                if (bHex.length() == 1) {
                    bHex = "0" + bHex;
                }
                String hexString = "#" + rHex + gHex + bHex;
                hex.setText(hexString);

                color = Color.rgb(r,g,b);
                colorField.setBackgroundColor(color);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragged = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isDragged = false;
        }
    };

    final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            isTyped = true;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String hexColor = hex.getText().toString();
            if (!isDragged) {
                if(hexColor.length() > 5) {
                    if(!hexColor.contains("#") && hexColor.length() == 6 && pattern.matcher(hexColor).matches()) {
                        hexColor = "#" + hexColor;
                        setColorFromRGB(hexColor);
                    } else if (hexColor.contains("#") && hexColor.length() == 7 && pattern.matcher(hexColor).matches()) {
                        setColorFromRGB(hexColor);
                    } else {
                        setBlack();
                    }
                } else {
                    setBlack();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            isTyped = false;
        }
    };

    public void setColorFromRGB(String hexColor) {
        color = Color.parseColor(hexColor);
        colorField.setBackgroundColor(color);
        hexColor = hexColor.replace("#","");
        int r = Integer.parseInt(hexColor.substring(0,2),16);
        int g = Integer.parseInt(hexColor.substring(2,4),16);
        int b = Integer.parseInt(hexColor.substring(4,6),16);
        sliderRed.setProgress(Math.round(r/2.55f));
        sliderGreen.setProgress(Math.round(g/2.55f));
        sliderBlue.setProgress(Math.round(b/2.55f));
    }

    public void setBlack() {
        color = Color.parseColor("#000000");
        colorField.setBackgroundColor(color);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ColorPickerDialog.ReturnColor) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogListener");
        }
    }

    public interface ReturnColor {
        void returnColor(int range, int color);
    }

}

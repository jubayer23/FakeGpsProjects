package com.app.fakegps.alertbanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.app.fakegps.appdata.MydApplication;

/**
 * Created by comsol on 11-May-18.
 */

public class NumberPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setLayoutParams(new LinearLayout.LayoutParams(10, 100));
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(25);
        numberPicker.setValue(MydApplication.getInstance().getPrefManger().getMockSpeed());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Joystick speed (m/s)");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
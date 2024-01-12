package com.suryodayach.bluetooth.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.suryodayach.bluetooth.model.FilterOptions;
import com.suryodayach.bluetooth.utils.DeviceTypes;

public class FilterDialog {

    public interface FilterDialogListener {
        void onFilterApplied(FilterOptions filterOptions);
    }

    public static void showFilterDialog(Context context, FilterOptions currentOptions, FilterDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Filter Options");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Adding checkboxes for device types
        for (String deviceType : DeviceTypes.getAllTypes()) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(deviceType);
            checkBox.setChecked(currentOptions.getSelectedTypes().contains(deviceType));
            layout.addView(checkBox);
        }

        // Adding dropdown for paired status
        Spinner pairedStatusSpinner = new Spinner(context);
        ArrayAdapter<String> pairedStatusAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
        pairedStatusAdapter.add("All");
        pairedStatusAdapter.add("Paired");
        pairedStatusAdapter.add("Unpaired");
        pairedStatusSpinner.setAdapter(pairedStatusAdapter);


        layout.addView(pairedStatusSpinner);

        builder.setView(layout);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            // Retrieving selected options from the dialog
            FilterOptions filterOptions = new FilterOptions();
            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) layout.getChildAt(i);
                    if (checkBox.isChecked()) {
                        filterOptions.addSelectedType(checkBox.getText().toString());
                    }
                }
            }
            // Retrieving selected paired status from the dropdown
            filterOptions.setSelectedPairedStatus(pairedStatusSpinner.getSelectedItem().toString());

            if (listener != null) {
                listener.onFilterApplied(filterOptions);
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}

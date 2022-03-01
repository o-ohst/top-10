package com.example.hst.top10downloader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by hst on 12/11/2017.
 */

public class WarningDialogue extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("WARNING")
                .setMessage("This app is extremely useful. DO NOT be overwhelmed.\n-HST")
                .setPositiveButton("OK", null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
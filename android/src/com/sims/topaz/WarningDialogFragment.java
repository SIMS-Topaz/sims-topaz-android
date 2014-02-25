package com.sims.topaz;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class WarningDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	    // Get the layout inflater
    	    LayoutInflater inflater = getActivity().getLayoutInflater();

    	    return builder.create();
    }
}
package com.sims.topaz;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class WarningDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	 
    	    // Get the layout inflater
    	    LayoutInflater inflater = getActivity().getLayoutInflater();
    	    // Inflate and set the layout for the dialog
    	    // Pass null as the parent view because its going in the dialog layout
    	    builder.setView(inflater.inflate(R.layout.fragment_dialog_warning, null))
    	    // Add action buttons
    	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    	               @Override
    	               public void onClick(DialogInterface dialog, int id) {
    	                   WarningDialogFragment.this.getDialog().cancel();
    	               }
    	           });
    	    return builder.create();
    }
}
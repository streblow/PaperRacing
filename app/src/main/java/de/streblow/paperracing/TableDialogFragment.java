package de.streblow.paperracing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by streblow on 11.12.2017.
 */

public class TableDialogFragment extends DialogFragment {

    public String header;
    public String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_table, container, false);
        setRetainInstance(true);
        header = getArguments().getString("header", "header");
        data = getArguments().getString("data", "");
        TableView tableView = (TableView)view.findViewById(R.id.cvTableView);
        tableView.updateResources(header, data, 60.0f);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}

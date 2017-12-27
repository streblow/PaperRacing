package de.streblow.paperracing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

/**
 * Created by streblow on 11.12.2017.
 */

public class TableDialogFragment extends DialogFragment implements View.OnClickListener {

    public String header;
    public String data;
    public String currentplayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_table, container, false);
        setRetainInstance(true);
        header = getArguments().getString("header", "header");
        data = getArguments().getString("data", "");
        currentplayer = getArguments().getString("currentplayer", "");
        TableView tableView = (TableView)view.findViewById(R.id.cvTableView);
        tableView.updateResources(header, data, currentplayer, 20.0f);
        ImageButton button = (ImageButton)view.findViewById(R.id.imageButtonClear);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dialog.getWindow().setLayout(Math.min((int)(size.x * 0.9), (int)(size.y * 0.9)),
                Math.min((int)(size.x * 0.9), (int)(size.y * 0.9)));
        dialog.getWindow().setGravity(Gravity.CENTER);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        window.setLayout(Math.min((int)(size.x * 0.9), (int)(size.y * 0.9)),
                Math.min((int)(size.x * 0.9), (int)(size.y * 0.9)));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).clearRaceStats();
        data = "";
        TableView view = (TableView)getView().findViewById(R.id.cvTableView);
        view.updateResources(header, data, currentplayer, 20.0f);
        view.invalidate();
    }

}

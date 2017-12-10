package de.streblow.paperracing;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by streblow on 24.11.2017.
 */

public class NewRaceDialogFragment extends DialogFragment {

    public interface OnDialogFragmentDismissedListener {
        void onDialogFragmentDismissedListener(int[] types, String[] names);
    }

    public String title;
    public OnDialogFragmentDismissedListener listener;
    private String arr_type[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_newrace, container, false);
        setRetainInstance(true);
        title = getArguments().getString("title", "");
        getDialog().setTitle(title);
        arr_type = new String[2];
        arr_type[0] = getString(R.string.quickrace_player_type_computer);
        arr_type[1] = getString(R.string.quickrace_player_type_human);
        ArrayAdapter adapter;
        Spinner spinner;
        spinner = (Spinner)view.findViewById(R.id.spinner1);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner2);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner3);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner4);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner5);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner6);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner7);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        spinner = (Spinner)view.findViewById(R.id.spinner8);
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.type_entry, arr_type);
        spinner.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        try {
            if (activity instanceof OnDialogFragmentDismissedListener)
                listener = (OnDialogFragmentDismissedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogFragmentDismissedListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnDialogFragmentDismissedListener)
                listener = (OnDialogFragmentDismissedListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDialogFragmentDismissedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        List<Integer> types = new ArrayList<>();
        List<String> names = new ArrayList<>();
        // create types and names from edittexts and spinners
        CheckBox checkbox1  = (CheckBox) getView().findViewById(R.id.checkBox1);
        Spinner spinner1 = (Spinner)getView().findViewById(R.id.spinner1);
        EditText edittext1 = (EditText)getView().findViewById(R.id.editText1);
        CheckBox checkbox2  = (CheckBox) getView().findViewById(R.id.checkBox2);
        Spinner spinner2 = (Spinner)getView().findViewById(R.id.spinner2);
        EditText edittext2 = (EditText)getView().findViewById(R.id.editText2);
        CheckBox checkbox3  = (CheckBox) getView().findViewById(R.id.checkBox3);
        Spinner spinner3 = (Spinner)getView().findViewById(R.id.spinner3);
        EditText edittext3 = (EditText)getView().findViewById(R.id.editText3);
        CheckBox checkbox4  = (CheckBox) getView().findViewById(R.id.checkBox4);
        Spinner spinner4 = (Spinner)getView().findViewById(R.id.spinner4);
        EditText edittext4 = (EditText)getView().findViewById(R.id.editText4);
        CheckBox checkbox5  = (CheckBox) getView().findViewById(R.id.checkBox5);
        Spinner spinner5 = (Spinner)getView().findViewById(R.id.spinner5);
        EditText edittext5 = (EditText)getView().findViewById(R.id.editText5);
        CheckBox checkbox6  = (CheckBox) getView().findViewById(R.id.checkBox6);
        Spinner spinner6 = (Spinner)getView().findViewById(R.id.spinner6);
        EditText edittext6 = (EditText)getView().findViewById(R.id.editText6);
        CheckBox checkbox7  = (CheckBox) getView().findViewById(R.id.checkBox7);
        Spinner spinner7 = (Spinner)getView().findViewById(R.id.spinner7);
        EditText edittext7 = (EditText)getView().findViewById(R.id.editText7);
        CheckBox checkbox8  = (CheckBox) getView().findViewById(R.id.checkBox8);
        Spinner spinner8 = (Spinner)getView().findViewById(R.id.spinner8);
        EditText edittext8 = (EditText)getView().findViewById(R.id.editText8);
        if (checkbox1.isChecked()) {

            types.add(spinner1.getSelectedItemPosition());
            names.add(edittext1.getText().toString());
        }
        if (checkbox2.isChecked()) {

            types.add(spinner2.getSelectedItemPosition());
            names.add(edittext2.getText().toString());
        }
        if (checkbox3.isChecked()) {

            types.add(spinner3.getSelectedItemPosition());
            names.add(edittext3.getText().toString());
        }
        if (checkbox4.isChecked()) {

            types.add(spinner4.getSelectedItemPosition());
            names.add(edittext4.getText().toString());
        }
        if (checkbox5.isChecked()) {

            types.add(spinner5.getSelectedItemPosition());
            names.add(edittext5.getText().toString());
        }
        if (checkbox6.isChecked()) {

            types.add(spinner6.getSelectedItemPosition());
            names.add(edittext6.getText().toString());
        }
        if (checkbox7.isChecked()) {

            types.add(spinner7.getSelectedItemPosition());
            names.add(edittext7.getText().toString());
        }
        if (checkbox8.isChecked()) {

            types.add(spinner8.getSelectedItemPosition());
            names.add(edittext8.getText().toString());
        }
        int[] t = new int[types.size()];
        String[] s = new String[names.size()];
        int p = 1;
        int c = 1;
        for (int i = 0; i < types.size(); i++) {
            t[i] = types.get(i);
            s[i] = names.get(i);
            if (s[i] == null || s[i].isEmpty())
                if (t[i] == Player.HUM)
                    s[i] = getResources().getString(R.string.name_player) + p++;
                else
                    s[i] = getResources().getString(R.string.name_computer) + c++;
            else
                s[i] = names.get(i);
        }
        listener.onDialogFragmentDismissedListener(t, s);
    }

}

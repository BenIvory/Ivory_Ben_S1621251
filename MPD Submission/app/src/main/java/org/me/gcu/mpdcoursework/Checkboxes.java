package org.me.gcu.mpdcoursework;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


public class Checkboxes extends Fragment {


    private CheckBox checkboxCI;
    private CheckBox checkboxCR;
    private CheckBox checkboxPR;

    public static Checkboxes newInstance() {
        Checkboxes fragment = new Checkboxes();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_checkboxes, container, false);

        checkboxCI = v.findViewById(R.id.checkBoxCI);
        checkboxCR = v.findViewById(R.id.checkBoxCR);
        checkboxPR = v.findViewById(R.id.checkBoxPR);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    protected boolean isChecked(int checkboxID) {
        switch (checkboxID) {
            case 0:
                return checkboxCI.isChecked();
            case 1:
                return checkboxCR.isChecked();
            case 2:
                return checkboxPR.isChecked();
            default:
                return false;
        }
    }
}

package com.swych.mobile.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.swych.mobile.R;

/**
 * Created by Manu on 1/21/2016.
 */
public class DefinitionFragment extends DialogFragment {
    TextView textView;


    public DefinitionFragment(){

    }

    public static DefinitionFragment newInstance(Bundle args){
        DefinitionFragment fragment = new DefinitionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.definition_fragment_layout, container);
        Button dismiss = (Button) view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
//        textView = (TextView) view.findViewById(R.id.textview);
        // Fetch arguments from bundle and set title
//        String title = getArguments().getString("title", null);
//        String text = getArguments().getString("text",null);
        getDialog().setTitle("sample fragment");
        textView.setText("sample fragment");

    }





}

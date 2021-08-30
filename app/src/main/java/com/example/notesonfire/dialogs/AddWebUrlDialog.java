package com.example.notesonfire.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.notesonfire.R;

public class AddWebUrlDialog extends DialogFragment implements View.OnClickListener {

    private EditText editTextAddUrlDialog;
    private TextView textViewAddDialog;
    private TextView textViewCancelAddDialog;
    private WebUrlListener webUrlListener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_web_url, container, false);
        editTextAddUrlDialog = view.findViewById(R.id.editTextAddUrlDialog);
        textViewAddDialog = view.findViewById(R.id.textViewAddUrl);
        textViewCancelAddDialog = view.findViewById(R.id.textViewCancelAddDialog);
        textViewAddDialog.setOnClickListener(this);
        textViewCancelAddDialog.setOnClickListener(this);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextAddUrlDialog.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewAddUrl:
                addUrl();
                dismiss();
                break;
            case R.id.textViewCancelAddDialog:
                dismiss();
                break;
        }
    }

    private void addUrl() {
        String webUrl = editTextAddUrlDialog.getText().toString().trim();
        webUrlListener.onWebUrlListener(webUrl);
    }

    public interface WebUrlListener {
        void onWebUrlListener(String url);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        webUrlListener = (WebUrlListener) context;
    }
}

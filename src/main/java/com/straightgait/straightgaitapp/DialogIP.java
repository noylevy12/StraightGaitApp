package com.straightgait.straightgaitapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogIP extends AppCompatDialogFragment {

    private EditText editTxtIpAddress;
    private DialogIPListener listener;

    public DialogIP(DialogIPListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        editTxtIpAddress = (EditText) view.findViewById(R.id.editTxtIpAddress);

        builder.setView(view)
                .setTitle("Connect to device")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ip = editTxtIpAddress.getText().toString();
                        listener.applyTextFromDialog(ip);

                    }
                });

        return builder.create();
    }


    public interface DialogIPListener{
        void applyTextFromDialog(String ip);
    }
}

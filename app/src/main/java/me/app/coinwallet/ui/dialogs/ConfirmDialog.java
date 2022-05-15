package me.app.coinwallet.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import me.app.coinwallet.R;

import java.util.Objects;

public class ConfirmDialog extends DialogFragment {
    private String message;
    private String title;
    private View view;
    private final OnSubmitListener callback;

    public ConfirmDialog(String message, String title, OnSubmitListener callback){
        super();
        this.message = message;
        this.title = title;
        this.callback = callback;
    }

    public ConfirmDialog(View view, OnSubmitListener callback){
        this.view = view;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        if(view != null){
            builder.setView(view);
        } else {
            builder.setMessage(message).setTitle(title);
        }
        builder.setCancelable(false)
                .setPositiveButton(R.string.positive_dialog_button, (dialog, id) -> {
                    dialog.dismiss();
                    callback.onConfirm();
                })
                .setNegativeButton(R.string.negative_dialog_button, ((dialog, id) -> {
                    dialog.dismiss();
                    callback.onCancel();
                }));
        return builder.create();
    }

    public interface OnSubmitListener{
        void onConfirm();
        void onCancel();
    }
}

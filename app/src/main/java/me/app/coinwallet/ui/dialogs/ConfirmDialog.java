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
    private final Runnable onConfirmCallback;
    private View view;

    public ConfirmDialog(String message, String title, Runnable onConfirmCallback){
        super();
        this.message = message;
        this.title = title;
        this.onConfirmCallback = onConfirmCallback;
    }

    public ConfirmDialog(View view, Runnable onConfirmCallback){
        this.view = view;
        this.onConfirmCallback = onConfirmCallback;
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
                    onConfirmCallback.run();
                })
                .setNegativeButton(R.string.negative_dialog_button, ((dialog, id) -> dialog.dismiss()));
        return builder.create();
    }
}

package me.app.coinwallet.ui.dialogs;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import me.app.coinwallet.R;

import java.util.function.Consumer;

public class CustomDialog {
    private static final int SINGLE_EDIT_TEXT_LAYOUT = R.layout.single_edit_text_dialog;

    public static ConfirmDialog labelDialog(LayoutInflater inflater, Consumer<String> onLabelSubmit){
        String message = "Input your label";
        String label = "Label";
        int inputType = EditorInfo.TYPE_CLASS_TEXT;
        return singleTextDialog(inflater, onLabelSubmit, message, label, inputType);
    }

    public static ConfirmDialog passwordDialog(LayoutInflater inflater, Consumer<String> onPasswordSubmit){
        String message = "Input your password";
        String label = "Password";
        int inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
        return singleTextDialog(inflater, onPasswordSubmit, message, label, inputType);
    }

    private static ConfirmDialog singleTextDialog(LayoutInflater inflater, Consumer<String> callback, String message,
                                                  String label, int inputType){
        View view = inflater.inflate(SINGLE_EDIT_TEXT_LAYOUT, null);
        EditText text = view.findViewById(R.id.dialog_edit_text);
        text.setInputType(inputType);
        TextView messageView = view.findViewById(R.id.dialog_message);
        messageView.setText(message);
        TextView labelView = view.findViewById(R.id.dialog_label);
        labelView.setText(label);
        return new ConfirmDialog(view, () -> callback.accept(text.getText().toString()));
    }
}

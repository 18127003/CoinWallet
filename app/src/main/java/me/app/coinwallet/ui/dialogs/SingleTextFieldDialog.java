package me.app.coinwallet.ui.dialogs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import me.app.coinwallet.R;


public class SingleTextFieldDialog extends ConfirmDialog {
    private static final int SINGLE_EDIT_TEXT_LAYOUT = R.layout.single_edit_text_dialog;

    private SingleTextFieldDialog(View view, DialogListener callback, TextInputEditText text) {
        super(view, new OnSubmitListener() {
            @Override
            public void onConfirm() {
                String result = "";
                if(text.getText()!=null){
                    result = text.getText().toString();
                }
                callback.onConfirm(result);
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }
        });
    }

    public static ConfirmDialog labelDialog(LayoutInflater inflater, DialogListener callback){
        String message = "Input your label";
        String label = "Label";
        int inputType = EditorInfo.TYPE_CLASS_TEXT;
        int iconMode = TextInputLayout.END_ICON_NONE;
        return singleTextDialog(inflater, callback, message, label, inputType,iconMode);
    }

    public static ConfirmDialog passwordDialog(LayoutInflater inflater, DialogListener callback){
        String message = "Input your password";
        String label = "Password";
        int iconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE;
        int inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
        return singleTextDialog(inflater, callback, message, label, inputType, iconMode);
    }

    private static ConfirmDialog singleTextDialog(LayoutInflater inflater, DialogListener callback,
                                                  String message, String label, int inputType, int endIconMode){
        View view = inflater.inflate(SINGLE_EDIT_TEXT_LAYOUT, null);
        TextInputEditText text = view.findViewById(R.id.dialog_edit_text);
        text.setInputType(inputType);
        TextView messageView = view.findViewById(R.id.dialog_message);
        messageView.setText(message);
        TextInputLayout textLayout = view.findViewById(R.id.dialog_edit_text_layout);
        textLayout.setHint(label);
        textLayout.setEndIconMode(endIconMode);

        return new SingleTextFieldDialog(view, callback, text);
    }

    public interface DialogListener {
        void onConfirm(String text);
        void onCancel();
    }
}

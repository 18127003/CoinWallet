package me.app.coinwallet.ui.fragments;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.SingleTextFieldDialog;

public class AuthenticateHandler {
    private final Configuration configuration;
    private final Fragment fragment;
    private final AuthenticateResultCallback callback;

    public AuthenticateHandler(Fragment fragment, AuthenticateResultCallback callback){
        this.fragment = fragment;
        this.callback = callback;
        this.configuration = ((BaseActivity) fragment.requireActivity()).configuration;
    }

    protected void authenticateAccess(){
        if(configuration.isFingerprintEnabled()){
            configuration.biometricUtil.authenticate(fragment, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    callback.onBiometricVerified();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    callback.onBiometricDenied();
                }

                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    accessPasswordDialog();
                }
            });
        } else {
            accessPasswordDialog();
        }
    }

    protected void accessPasswordDialog(){
        ConfirmDialog dialog = SingleTextFieldDialog.passwordDialog(fragment.getLayoutInflater(),
                new SingleTextFieldDialog.DialogListener() {
                    @Override
                    public void onConfirm(String text) {
                        if(checkPassword(text)){
                            callback.onPasswordVerified(text);
                        } else {
                            callback.onPasswordDenied();
                        }
                    }

                    @Override
                    public void onCancel() {
                        callback.onPasswordDenied();
                    }
                });
        dialog.show(fragment.requireActivity().getSupportFragmentManager(), "password_access");
    }

    public interface AuthenticateResultCallback{
        default void onBiometricVerified(){}
        default void onBiometricDenied(){}
        void onPasswordVerified(String password);
        void onPasswordDenied();
    }

    private boolean checkPassword(String password){
        LocalWallet localWallet = LocalWallet.getInstance();
        try{
            return localWallet.checkPassword(password);
        } catch (IllegalStateException e){
            localWallet.encryptWallet(password);
            return true;
        }
    }
}

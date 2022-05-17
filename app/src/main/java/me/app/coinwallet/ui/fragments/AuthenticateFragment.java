package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.SingleTextFieldDialog;
import me.app.coinwallet.viewmodels.AuthenticateViewModel;

public abstract class AuthenticateFragment extends Fragment {
    private AuthenticateViewModel authenticateViewModel;
    protected Configuration configuration;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authenticateViewModel = new ViewModelProvider(requireActivity()).get(AuthenticateViewModel.class);
        configuration = ((BaseActivity) requireActivity()).configuration;
    }

    protected void authenticateAccess(){
        if(configuration.isFingerprintEnabled()){
            configuration.biometricUtil.authenticate(this, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    onBiometricVerified();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    onBiometricDenied();
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
        ConfirmDialog dialog = SingleTextFieldDialog.passwordDialog(getLayoutInflater(),
                new SingleTextFieldDialog.DialogListener() {
                    @Override
                    public void onConfirm(String text) {
                        if(authenticateViewModel.checkPassword(text)){
                            onPasswordVerified(text);
                        } else {
                            onPasswordDenied();
                        }
                    }

                    @Override
                    public void onCancel() {
                        onPasswordDenied();
                    }
                });
        dialog.show(requireActivity().getSupportFragmentManager(), "password_access");
    }

    protected void onBiometricVerified(){}

    protected void onBiometricDenied(){}

    protected abstract void onPasswordVerified(String password);

    protected abstract void onPasswordDenied();
}

package me.app.coinwallet.utils;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class BiometricUtil {
    private final FragmentActivity activity;
    private final BiometricManager biometricManager;
    private final ToastUtil toastUtil;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;

    public BiometricUtil(FragmentActivity activity){
        this.activity = activity;
        biometricManager = BiometricManager.from(this.activity);
        toastUtil = new ToastUtil(activity);
    }

    private static final int OPTION = (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)?
            (BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL):
            (BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL);

    public void setAuthenticationCallback(BiometricPrompt.AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
    }

    public BiometricPrompt getBiometricPrompt(){
        if(authenticationCallback==null){
            authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            };
        }
        return new BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), authenticationCallback);
    }

    public BiometricPrompt.PromptInfo getPromptInfo(@NonNull String title, String subtitle, String description){
        BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(OPTION);
        return builder.build();
    }

    public boolean canAuthenticate(){
        switch (biometricManager.canAuthenticate(OPTION)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("HD", "App can authenticate using biometrics.");
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("HD", "No biometric features available on this device.");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("HD", "Biometric features are currently unavailable.");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("HD", "No biometric sign enrolled");
                toastUtil.postToast("There is no lock screen set on this device", Toast.LENGTH_SHORT);
                return false;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                Log.e("HD","Unsupported option for current API");
                return false;
            default:
                return false;
        }
    }
}

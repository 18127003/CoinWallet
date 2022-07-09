package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.viewmodels.SettingViewModel;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText newPassword;
    private TextInputEditText confirmPassword;
    private Button changeBtn;
    private SettingViewModel viewModel;
    private AuthenticateHandler authenticateHandler;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        authenticateHandler = new AuthenticateHandler(this, new AuthenticateHandler.AuthenticateResultCallback() {
            @Override
            public void onPasswordVerified(String password) {
                String newPwd = newPassword.getText().toString();
                String confirmPwd = confirmPassword.getText().toString();
                if(viewModel.changePassword(password, newPwd, confirmPwd)){
                    ((BaseActivity) requireActivity()).configuration.toastUtil.postToast("Password changed", Toast.LENGTH_SHORT);
                    requireActivity().finish();
                } else {
                    ((BaseActivity) requireActivity()).configuration.toastUtil.postToast("Confirmation password not match", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onPasswordDenied() {
                ((BaseActivity) requireActivity()).configuration.toastUtil.postToast("Wrong password", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        newPassword = view.findViewById(R.id.new_password_text_field);
        confirmPassword = view.findViewById(R.id.confirm_password_text_field);
        changeBtn = view.findViewById(R.id.change_password_button);
        changeBtn.setOnClickListener(v-> authenticateHandler.accessPasswordDialog());
    }
}
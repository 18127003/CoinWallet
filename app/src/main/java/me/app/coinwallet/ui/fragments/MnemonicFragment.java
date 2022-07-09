package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.viewmodels.SettingViewModel;

public class MnemonicFragment extends Fragment {
    private AuthenticateHandler authenticateHandler;
    private AuthenticateHandler saveAuthHandler;
    private SettingViewModel viewModel;
    private TextView mnemonic;
    private Button saveBtn;
    private Configuration configuration;

    public MnemonicFragment() {
        // Required empty public constructor
    }

    public static MnemonicFragment newInstance() {
        return new MnemonicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = ((BaseActivity) requireActivity()).configuration;
        authenticateHandler = new AuthenticateHandler(this, new AuthenticateHandler.AuthenticateResultCallback() {
            @Override
            public void onPasswordVerified(String password) {
                viewModel.decryptMnemonic(password);
                saveBtn.setClickable(true);
            }

            @Override
            public void onPasswordDenied() {
                requireActivity().finish();
            }
        });
        saveAuthHandler = new AuthenticateHandler(this, new AuthenticateHandler.AuthenticateResultCallback() {
            @Override
            public void onPasswordVerified(String password) { }

            @Override
            public void onPasswordDenied() { }

            @Override
            public void onBiometricVerified() {
                if(viewModel.encryptMnemonic()){
                    configuration.toastUtil.postToast("Mnemonic encrypted and saved", Toast.LENGTH_SHORT);
                } else {
                    configuration.toastUtil.postToast("Mnemonic save failed", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mnemonic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        mnemonic = view.findViewById(R.id.mnemonic);
        saveBtn = view.findViewById(R.id.save_mnemonic_button);
        viewModel.getMnemonic().observe(this, s->mnemonic.setText(s));
        saveBtn.setOnClickListener(v-> saveAuthHandler.authenticateAccess());
        authenticateHandler.accessPasswordDialog();
    }
}
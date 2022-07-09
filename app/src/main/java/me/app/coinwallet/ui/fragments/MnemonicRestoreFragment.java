package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.RestoreMnemonicAdapter;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import org.bitcoinj.wallet.UnreadableWalletException;

public class MnemonicRestoreFragment extends Fragment {
    private InitPageViewModel viewModel;
    private Button cancelBtn;
    private Button restoreBtn;
    private RecyclerView mnemonicLabels;
    private BiometricUtil biometricUtil;
    private TextInputEditText mnemonicText;
    private Configuration configuration;

    public MnemonicRestoreFragment() {
        // Required empty public constructor
    }

    public static MnemonicRestoreFragment newInstance() {
        MnemonicRestoreFragment fragment = new MnemonicRestoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = ((BaseActivity) requireActivity()).configuration;
        biometricUtil = configuration.biometricUtil;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mnemonic_restore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = view.findViewById(R.id.cancel_button);
        restoreBtn = view.findViewById(R.id.restore_button);
        mnemonicText = view.findViewById(R.id.mnemonic_text_field);
        mnemonicLabels = view.findViewById(R.id.restore_mnemonic_list);
        viewModel = new ViewModelProvider(requireActivity()).get(InitPageViewModel.class);
        cancelBtn.setOnClickListener(v->((BaseActivity) requireActivity()).loadFragment(SelectWalletFragment.class));
        restoreBtn.setOnClickListener(v->{
            try {
                viewModel.restoreWallet(mnemonicText.getText().toString());
                ((BaseActivity) requireActivity()).loadFragment(SyncFragment.class);
            } catch (UnreadableWalletException e) {
                configuration.toastUtil.postToast("Mnemonic not available", Toast.LENGTH_SHORT);
            }
        });
        RestoreMnemonicAdapter adapter = new RestoreMnemonicAdapter(new BaseAdapter.OnItemClickListener<String>() {
            @Override
            public void onClick(String item) {
                biometricUtil.authenticate(MnemonicRestoreFragment.this, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String mnemonic = viewModel.decryptMnemonic(item);
                        viewModel.setSelectedWalletLabel(item);
                        mnemonicText.setText(mnemonic);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });
            }
        });
        mnemonicLabels.setAdapter(adapter);
        mnemonicLabels.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.getMnemonicLabels().observe(this, adapter::update);
    }
}
package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
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
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.RestoreMnemonicAdapter;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import me.app.coinwallet.viewmodels.RestoreMnemonicViewModel;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MnemonicRestoreFragment extends Fragment {
    private RestoreMnemonicViewModel viewModel;
    private Button restoreBtn;
    private RecyclerView mnemonicLabels;
    private BiometricUtil biometricUtil;
    private TextInputEditText mnemonicText;
    private LinearLayout accountInfosLayout;
    private Button moreAccountBtn;
    private Configuration configuration;

    public MnemonicRestoreFragment() {
        // Required empty public constructor
    }

    public static MnemonicRestoreFragment newInstance() {
        return new MnemonicRestoreFragment();
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
        restoreBtn = view.findViewById(R.id.restore_button);
        mnemonicText = view.findViewById(R.id.mnemonic_text_field);
        mnemonicLabels = view.findViewById(R.id.restore_mnemonic_list);
        accountInfosLayout = view.findViewById(R.id.accounts_layout);
        moreAccountBtn = view.findViewById(R.id.more_account_btn);
        viewModel = new ViewModelProvider(requireActivity()).get(RestoreMnemonicViewModel.class);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 40, 0, 0);

        moreAccountBtn.setOnClickListener((v)->{
            View accountInfoView = getLayoutInflater().inflate(R.layout.account_restore_info, null);
            accountInfosLayout.addView(accountInfoView, layoutParams);
        });

        RestoreMnemonicAdapter adapter = new RestoreMnemonicAdapter(new BaseAdapter.OnItemClickListener<String>() {
            @Override
            public void onClick(String item) {
                biometricUtil.authenticate(MnemonicRestoreFragment.this, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String mnemonic = viewModel.decryptMnemonic(item);
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

        List<String> items = new ArrayList<>(Constants.SUPPORTED_BLOCKCHAIN.keySet());
        ArrayAdapter<String> networkAdapter = new ArrayAdapter<>(requireContext(), R.layout.coin_select_item, items);
        AutoCompleteTextView network = view.findViewById(R.id.select_option);
        network.setAdapter(networkAdapter);

        restoreBtn.setOnClickListener(v->{
            String networkId = Constants.SUPPORTED_BLOCKCHAIN.get(network.getText().toString());
            if(networkId == null || mnemonicText.getText() == null){
                return;
            }
            NetworkParameters parameters = NetworkParameters.fromID(networkId);
            String mnemonic = mnemonicText.getText().toString();
            InputStream checkpoints = configuration.getBlockchainCheckpointFile();
            List<WalletInfoEntry> accounts = accounts(parameters);
            List<HDPath> paths = accountPaths(parameters, accounts);
            try {
                // config wallet info
                viewModel.restoreWallet(parameters, mnemonic, paths, checkpoints);
                // store labels
                viewModel.saveWalletInfos(accounts);
                // restore commence
                ((BaseActivity) requireActivity()).loadFragment(RestoreWalletProgressFragment.class);
            } catch (UnreadableWalletException e) {
                configuration.toastUtil.postToast("Fail to construct seed from mnemonic", Toast.LENGTH_SHORT);
            }
        });
    }

    List<HDPath> accountPaths(NetworkParameters parameters, List<WalletInfoEntry> accounts){
        return accounts.stream().map(account->Constants.WALLET_STRUCTURE.accountPathFor(parameters, account.getAccountIndex()))
                .collect(Collectors.toList());
    }

    List<WalletInfoEntry> accounts(NetworkParameters parameters){
        List<WalletInfoEntry> entries = new ArrayList<>();
        for (int i = 0; i<accountInfosLayout.getChildCount(); i++){
            View accountInfoView = accountInfosLayout.getChildAt(i);
            TextInputEditText label = accountInfoView.findViewById(R.id.account_label);
            TextInputEditText index = accountInfoView.findViewById(R.id.account_index);
            int accountIndex = Integer.parseInt(index.getText().toString());
            entries.add(new WalletInfoEntry(accountIndex, parameters.getId(), label.getText().toString()));
        }
        return entries;
    }
}
package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import me.app.coinwallet.R;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.MainActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.RestoreMnemonicAdapter;
import me.app.coinwallet.ui.adapters.WalletInfoAdapter;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.viewmodels.InitPageViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MnemonicRestoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MnemonicRestoreFragment extends Fragment implements BaseAdapter.OnItemClickListener<String> {
    private InitPageViewModel viewModel;
    private Button cancelBtn;
    private RecyclerView mnemonicLabels;
    private BiometricUtil biometricUtil;

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
        biometricUtil = new BiometricUtil(requireActivity());
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
        mnemonicLabels = view.findViewById(R.id.restore_mnemonic_list);
        viewModel = new ViewModelProvider(requireActivity()).get(InitPageViewModel.class);
        cancelBtn.setOnClickListener(v->((BaseActivity) requireActivity()).loadFragment(SelectWalletFragment.newInstance()));
        RestoreMnemonicAdapter adapter = new RestoreMnemonicAdapter(this);
        mnemonicLabels.setAdapter(adapter);
        mnemonicLabels.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.getMnemonicLabels().observe(this, adapter::update);
    }

    @Override
    public void onClick(String item) {
        biometricUtil.setAuthenticationCallback(new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String mnemonic = viewModel.decryptMnemonic(item);
                Log.e("HD","Decrypted value: "+mnemonic);
                restoreAndSync(item, mnemonic);
            }
        });
        biometricUtil.authenticate();
    }

    public void restoreAndSync(String label, String mnemonic){
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("label", label);
        intent.putExtra("mnemonic", mnemonic);
        startActivity(intent);
    }
}
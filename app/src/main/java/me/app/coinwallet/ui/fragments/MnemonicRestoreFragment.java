package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
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
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.RestoreMnemonicAdapter;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import org.bitcoinj.wallet.UnreadableWalletException;

public class MnemonicRestoreFragment extends Fragment {
    private InitPageViewModel viewModel;
    private Button restoreBtn;
    private RecyclerView mnemonicLabels;
    private BiometricUtil biometricUtil;
    private TextInputEditText mnemonicText;
    private String label;
    private final ActivityResultLauncher<String> askLabel = registerForActivityResult(
            new ActivityResultContract<String, String>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, String input) {
                    return SingleFragmentActivity.newActivity(context, CreateWalletFragment.class, "Create wallet label");
                }
                @Override
                public String parseResult(int resultCode, @Nullable Intent intent) {
                    if(resultCode == Activity.RESULT_OK && intent!=null){
                        return intent.getStringExtra(Constants.WALLET_LABEL_EXTRA_NAME);
                    }
                    return "wallet";
                }
            },
            new ActivityResultCallback<String>() {
                @Override
                public void onActivityResult(String result) {
                    label = result;
                    result(label, mnemonicText.getText().toString());
                }
            }
    );

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
        biometricUtil = ((BaseActivity) requireActivity()).configuration.biometricUtil;
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
        viewModel = new ViewModelProvider(requireActivity()).get(InitPageViewModel.class);
        restoreBtn.setOnClickListener(v->{
            if(label==null){
                askLabel.launch(null);
            } else if (mnemonicText.getText() != null){
                result(label, mnemonicText.getText().toString());
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
                        label = item;
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

    void result(String label, String mnemonic){
        Intent result = new Intent();
        result.putExtra(Constants.WALLET_LABEL_EXTRA_NAME, label);
        result.putExtra(Constants.MNEMONIC_EXTRA_NAME, mnemonic);
        requireActivity().setResult(Activity.RESULT_OK, result);
        requireActivity().finish();
    }
}
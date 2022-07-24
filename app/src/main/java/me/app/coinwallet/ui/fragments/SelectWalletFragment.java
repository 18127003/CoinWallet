package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.WalletInfoAdapter;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import org.bitcoinj.wallet.UnreadableWalletException;

public class SelectWalletFragment extends Fragment{
    private InitPageViewModel viewModel;
    private Button createBtn;
    private Button restoreBtn;
    private final ActivityResultLauncher<String> askLabel = registerForActivityResult(
            new ActivityResultContract<String, String>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, String input) {
                    return SingleFragmentActivity.newActivity(context, CreateWalletFragment.class, "Create new wallet");
                }
                @Override
                public String parseResult(int resultCode, @Nullable Intent intent) {
                    if(resultCode == Activity.RESULT_OK && intent!=null){
                        return intent.getStringExtra(Constants.WALLET_LABEL_EXTRA_NAME);
                    }
                    return null;
                }
            },
            new ActivityResultCallback<String>() {
                @Override
                public void onActivityResult(String result) {
                    if(result!=null){
                        viewModel.saveWalletInfo(result);
                        viewModel.setSelectedWalletLabel(result);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> askMnemonic = registerForActivityResult(
            new ActivityResultContract<Intent, Intent>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, Intent input) {
                    return SingleFragmentActivity.newActivity(context, MnemonicRestoreFragment.class, "Restore wallet");
                }

                @Override
                public Intent parseResult(int resultCode, @Nullable Intent intent) {
                    if(resultCode == Activity.RESULT_OK && intent!=null){
                        return intent;
                    }
                    return new Intent();
                }
            },
            new ActivityResultCallback<Intent>() {
                @Override
                public void onActivityResult(Intent result) {
                    String label = result.getStringExtra(Constants.WALLET_LABEL_EXTRA_NAME);
                    String mnemonic = result.getStringExtra(Constants.MNEMONIC_EXTRA_NAME);
                    if(label != null && mnemonic != null){
                        try {
                            viewModel.restoreWallet(mnemonic);
                            viewModel.saveWalletInfo(label);
                            viewModel.setSelectedWalletLabel(label);
                        } catch (UnreadableWalletException e) {
                            ((BaseActivity) requireActivity()).configuration.toastUtil.postToast("Mnemonic unacceptable", Toast.LENGTH_SHORT);
                        }
                    }
                }
            }
    );

    public SelectWalletFragment() {
        // Required empty public constructor
    }

    public static SelectWalletFragment newInstance() {
        SelectWalletFragment fragment = new SelectWalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(InitPageViewModel.class);
        createBtn = view.findViewById(R.id.create_wallet_btn);
        restoreBtn = view.findViewById(R.id.restore_wallet_btn);
        RecyclerView walletInfoList = view.findViewById(R.id.wallet_info_list);
        WalletInfoAdapter adapter = new WalletInfoAdapter(item -> viewModel.setSelectedWalletLabel(item.getLabel()));
        walletInfoList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        walletInfoList.setAdapter(adapter);
        viewModel.getWalletInfos().observe(this, adapter::update);
        restoreBtn.setOnClickListener((v)-> askMnemonic.launch(null));
        createBtn.setOnClickListener((v)-> askLabel.launch(null));
        viewModel.getSelectedWalletLabel().observe(this, label->{
            if(label!=null){
                ((BaseActivity) requireActivity()).loadFragment(SyncFragment.class);
            }
        });
    }

}
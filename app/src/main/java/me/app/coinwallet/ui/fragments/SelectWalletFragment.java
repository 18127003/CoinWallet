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
import me.app.coinwallet.ui.adapters.WalletInfoAdapter;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import org.bitcoinj.wallet.UnreadableWalletException;

public class SelectWalletFragment extends Fragment{
    private InitPageViewModel viewModel;
    private Button createBtn;
    private Button restoreBtn;
    private final ActivityResultLauncher<WalletInfoEntry> askLabel = registerForActivityResult(
            new ActivityResultContract<WalletInfoEntry, WalletInfoEntry>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, WalletInfoEntry input) {
                    return SingleFragmentActivity.newActivity(context, CreateWalletFragment.class, "Create new wallet");
                }
                @Override
                public WalletInfoEntry parseResult(int resultCode, @Nullable Intent intent) {
                    if(resultCode == Activity.RESULT_OK && intent!=null){
                        return (WalletInfoEntry) intent.getSerializableExtra(Constants.WALLET_LABEL_EXTRA_NAME);
                    }
                    return null;
                }
            },
            new ActivityResultCallback<WalletInfoEntry>() {
                @Override
                public void onActivityResult(WalletInfoEntry result) {
                    if(result!=null){
                        WalletInfoEntry entry = viewModel.saveWalletInfo(result);
                        viewModel.setSelectedWallet(entry);
                    }
                }
            }
    );

    public SelectWalletFragment() {
        // Required empty public constructor
    }

    public static SelectWalletFragment newInstance() {
        return new SelectWalletFragment();
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
        WalletInfoAdapter adapter = new WalletInfoAdapter(item -> viewModel.setSelectedWallet(item));
        walletInfoList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        walletInfoList.setAdapter(adapter);
        viewModel.getWalletInfos().observe(this, adapter::update);
        restoreBtn.setOnClickListener((v)-> {
            ((BaseActivity) requireActivity()).loadFragmentOut(MnemonicRestoreFragment.class, R.string.restore_wallet_page_label);
        });
        createBtn.setOnClickListener((v)-> askLabel.launch(null));
        viewModel.getSelectedWallet().observe(this, walletInfo->{
            if(walletInfo!=null){
                Intent intent = SingleFragmentActivity.newActivity(requireContext(), SyncFragment.class, "");
                intent.putExtra("create_wallet", viewModel.createNewAccount);
                intent.putExtra("wallet_info", walletInfo);
                startActivity(intent);
            }
        });
    }

}
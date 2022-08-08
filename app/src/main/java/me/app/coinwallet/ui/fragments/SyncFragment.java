package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.blockchain.BlockchainSyncService;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.HomeActivity;
import me.app.coinwallet.viewmodels.InitPageViewModel;

import java.io.InputStream;

public class SyncFragment extends Fragment {

    private TextView status;
    private Configuration configuration;
    private InitPageViewModel viewModel;
    private AuthenticateHandler authenticateHandler;
    boolean createWallet = false;
    WalletInfoEntry walletInfoEntry;

    public SyncFragment() {
        // Required empty public constructor
    }

    public static SyncFragment newInstance() {
        return new SyncFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.configuration = ((BaseActivity) requireActivity()).configuration;
        authenticateHandler = new AuthenticateHandler(this, new AuthenticateHandler.AuthenticateResultCallback() {
            @Override
            public void onPasswordVerified(String password) {
                if(createWallet){
                    viewModel.addAccount(walletInfoEntry.getAccountIndex(), password);
                }
                toHomePage();
            }

            @Override
            public void onPasswordDenied() {
                requireActivity().finish();
                BlockchainSyncService.SHOULD_RESTART.set(false);
                BlockchainSyncService.stop();
            }

            @Override
            public void onBiometricVerified() {
                toHomePage();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sync, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        status = view.findViewById(R.id.status);
        viewModel = new ViewModelProvider(requireActivity()).get(InitPageViewModel.class);
        InputStream checkpoints = configuration.getBlockchainCheckpointFile();
        Intent intent = requireActivity().getIntent();
        createWallet = intent.getBooleanExtra("create_wallet", false);
        walletInfoEntry = (WalletInfoEntry) intent.getSerializableExtra("wallet_info");
        viewModel.initWallet(walletInfoEntry, checkpoints);
        viewModel.getStatus().observe(this, (i)->{
            status.setText(i.toString());
            if(i.equals(BlockchainLiveData.BlockchainStatus.SYNC_START)){
                if(createWallet){
                    authenticateHandler.accessPasswordDialog();
                }
                else if(viewModel.isEncrypted()){
                    authenticateHandler.authenticateAccess();
                } else {
                    authenticateHandler.accessPasswordDialog();
                }
            }
        });
        viewModel.startSync();
    }

    private void toHomePage(){
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
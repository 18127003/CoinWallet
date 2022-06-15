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
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.blockchain.BlockchainSyncService;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.HomeActivity;
import me.app.coinwallet.viewmodels.InitPageViewModel;

import java.io.InputStream;

public class SyncFragment extends Fragment {

    private TextView sync;
    private TextView status;
    private Configuration configuration;
    private InitPageViewModel viewModel;
    private AuthenticateHandler authenticateHandler;

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
                toHomePage();
            }

            @Override
            public void onPasswordDenied() {
                ((BaseActivity) requireActivity()).loadFragment(SelectWalletFragment.class);
                BlockchainSyncService.SHOULD_RESTART.set(false);
                Log.e("HD", BlockchainSyncService.SHOULD_RESTART.get()+" out");
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
        sync = view.findViewById(R.id.sync);
        status = view.findViewById(R.id.status);
        viewModel = new ViewModelProvider(requireActivity()).get(InitPageViewModel.class);
        InputStream checkpoints = ((BaseActivity) requireActivity()).configuration.getBlockchainCheckpointFile();
        viewModel.initWallet(configuration.directory, configuration.parameters, checkpoints);
        viewModel.getSyncProgress().observe(this, s -> sync.setText(s));
        viewModel.getStatus().observe(this, (i)->{
            status.setText(i);
            if(i.equals(R.string.app_setup_completed)){
                if(viewModel.isEncrypted()){
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
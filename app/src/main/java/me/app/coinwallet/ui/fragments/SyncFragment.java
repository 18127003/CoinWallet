package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.HomeActivity;
import me.app.coinwallet.viewmodels.InitPageViewModel;

import java.io.InputStream;

public class SyncFragment extends AuthenticateFragment {

    private TextView sync;
    private TextView status;
    private InitPageViewModel viewModel;

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
            if(i.equals(R.string.app_sync_completed)){
                if(viewModel.isEncrypted()){
                    authenticateAccess();
                } else {
                    accessPasswordDialog();
                }
            }
        });
        viewModel.startSync();
    }

    @Override
    protected void onBiometricVerified() {
        toHomePage();
    }

    @Override
    protected void onBiometricDenied() {
        this.onPasswordDenied();
    }

    @Override
    protected void onPasswordVerified(String password){
        toHomePage();
    }

    @Override
    protected void onPasswordDenied(){
        ((BaseActivity) requireActivity()).loadFragment(SelectWalletFragment.class);
        viewModel.cancelSync();
    }

    private void toHomePage(){
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
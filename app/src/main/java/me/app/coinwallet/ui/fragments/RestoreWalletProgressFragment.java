package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import me.app.coinwallet.viewmodels.RestoreMnemonicViewModel;

public class RestoreWalletProgressFragment extends Fragment {
    private RestoreMnemonicViewModel viewModel;

    public RestoreWalletProgressFragment() {
        // Required empty public constructor
    }

    public static RestoreWalletProgressFragment newInstance() {
        return new RestoreWalletProgressFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restore_wallet_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RestoreMnemonicViewModel.class);
        viewModel.getStatus().observe(this, i->{
            if(i.equals(BlockchainLiveData.BlockchainStatus.SYNCED)){
                requireActivity().finish();
            }
        });
        viewModel.startSync();
    }
}
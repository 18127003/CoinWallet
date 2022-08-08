package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.viewmodels.SettingViewModel;

public class WalletManageFragment extends Fragment {

    private TextView address;
    private TextView accountPath;
    private SettingViewModel viewModel;

    public WalletManageFragment() {
        // Required empty public constructor
    }

    public static WalletManageFragment newInstance() {
        return new WalletManageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        address = view.findViewById(R.id.address);
        accountPath = view.findViewById(R.id.account_path);
        accountPath.setText(viewModel.getAccountPath());
        viewModel.getAddress().observe(this, s->address.setText(s));
    }
}
package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import me.app.coinwallet.ui.adapters.WalletInfoAdapter;
import me.app.coinwallet.viewmodels.InitPageViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectWalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectWalletFragment extends Fragment implements BaseAdapter.OnItemClickListener<WalletInfoEntry>{
    private InitPageViewModel viewModel;
    private Button createBtn;
    private Button restoreBtn;

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
        WalletInfoAdapter adapter = new WalletInfoAdapter(this);
        walletInfoList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        walletInfoList.setAdapter(adapter);
        viewModel.getWalletInfos().observe(this, adapter::update);

        restoreBtn.setOnClickListener((v)-> {
            // TODO: select from list saved label - mnemonic preferences
//            String mnemonic = viewModel.restoreMnemonic("wallet");
//            String label = "wallet";
//            viewModel.saveWalletInfo(label);
//            moveToSync(label, mnemonic);
            ((BaseActivity) requireActivity()).loadFragment(MnemonicRestoreFragment.newInstance());
        });
        createBtn.setOnClickListener((v)-> {
            String label = "wallet";
            viewModel.saveWalletInfo(label);
            moveToSync(label, null);
        });
    }

    private void moveToSync(String label, String mnemonic){
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("label", label);
        if (mnemonic != null){
            intent.putExtra("mnemonic", mnemonic);
        }
        startActivity(intent);
    }

    @Override
    public void onClick(WalletInfoEntry walletInfoEntry) {
        moveToSync(walletInfoEntry.getLabel(), null);
    }
}
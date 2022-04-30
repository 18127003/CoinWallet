package me.app.coinwallet.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.adapters.WalletInfoAdapter;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import me.app.coinwallet.wallets.WalletInfoEntry;

public class InitActivity extends AppCompatActivity implements WalletInfoAdapter.OnItemClickListener {

    Button createBtn;
    InitPageViewModel viewModel;
    Button restoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        createBtn = findViewById(R.id.create_wallet_btn);
        restoreBtn = findViewById(R.id.restore_wallet_btn);
        RecyclerView walletInfoList = findViewById(R.id.wallet_info_list);
        viewModel = new ViewModelProvider(this).get(InitPageViewModel.class);
        WalletInfoAdapter adapter = new WalletInfoAdapter(this);
        walletInfoList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        walletInfoList.setAdapter(adapter);
        viewModel.getWalletInfos().observe(this, adapter::updateWalletInfos);

        restoreBtn.setOnClickListener((v)-> {
            String mnemonic = viewModel.restoreMnemonic();
            String label = "wallet";
            viewModel.saveWalletInfo(label);
            moveToSync(label, mnemonic);
        });
        createBtn.setOnClickListener((v)-> {
            String label = "wallet";
            viewModel.saveWalletInfo(label);
            moveToSync(label, null);
        });
    }

    public void moveToSync(String label, String mnemonic){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("label", label);
        if (mnemonic != null){
            intent.putExtra("mnemonic", mnemonic);
        }
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        viewModel.fetch();
    }

    @Override
    public void onClick(WalletInfoEntry walletInfoEntry) {
        moveToSync(walletInfoEntry.getLabel(), null);
    }
}
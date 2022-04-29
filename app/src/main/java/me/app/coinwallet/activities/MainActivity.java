package me.app.coinwallet.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.R;
import me.app.coinwallet.viewmodels.SetupPageViewModel;

public class MainActivity extends BaseActivity {
    private TextView sync;
    private TextView status;
    private WalletApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.application = getWalletApplication();
        setContentView(R.layout.activity_main);
        Intent comingIntent = getIntent();
        String label = comingIntent.getStringExtra("label");
        String mnemonic = comingIntent.getStringExtra("mnemonic");
        sync = findViewById(R.id.sync);
        status = findViewById(R.id.status);
        final SetupPageViewModel walletViewModel = new ViewModelProvider(this)
                .get(SetupPageViewModel.class);
        walletViewModel.initWallet(label, mnemonic);
        walletViewModel.getSyncProgress().observe(this, s -> sync.setText(s));
        walletViewModel.getStatus().observe(this, (i)->{
            status.setText(i);
            if(i.equals(R.string.app_sync_completed)){
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        });
        walletViewModel.startSync();
    }
}
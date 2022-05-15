package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.CustomDialog;
import me.app.coinwallet.viewmodels.SetupPageViewModel;

public class SetupActivity extends BaseActivity {
    private TextView sync;
    private TextView status;
    private SetupPageViewModel walletViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Intent comingIntent = getIntent();
        String label = comingIntent.getStringExtra("label");
        String mnemonic = comingIntent.getStringExtra("mnemonic");
        sync = findViewById(R.id.sync);
        status = findViewById(R.id.status);
        walletViewModel = new ViewModelProvider(this).get(SetupPageViewModel.class);
        walletViewModel.initWallet(label, mnemonic, configuration.directory, configuration.parameters);
        walletViewModel.getSyncProgress().observe(this, s -> sync.setText(s));
        walletViewModel.getStatus().observe(this, (i)->{
            status.setText(i);
            if(i.equals(R.string.app_sync_completed)){
                accessPasswordDialog();
            }
        });
        walletViewModel.startSync();
    }

    private void accessPasswordDialog(){
        ConfirmDialog dialog = CustomDialog.passwordDialog(getLayoutInflater(),
                (password) -> {
                    if(walletViewModel.checkPassword(password)){
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        onPasswordDeny();
                    }
                });
        dialog.show(getSupportFragmentManager(), "password_access");
    }

    private void onPasswordDeny(){
        finish();
    }
}
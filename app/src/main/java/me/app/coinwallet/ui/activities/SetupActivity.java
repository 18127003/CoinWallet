package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.SingleTextFieldDialog;
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
        ConfirmDialog dialog = SingleTextFieldDialog.passwordDialog(getLayoutInflater(),
                new SingleTextFieldDialog.DialogListener() {
                    @Override
                    public void onConfirm(String text) {
                        if(walletViewModel.checkPassword(text)){
                            onPasswordVerified();
                        } else {
                            onPasswordDenied();
                        }
                    }

                    @Override
                    public void onCancel() {
                        onPasswordDenied();
                    }
                });
        dialog.show(getSupportFragmentManager(), "password_access");
    }

    private void onPasswordVerified(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onPasswordDenied(){
        finish();
        walletViewModel.cancelSync();
    }
}
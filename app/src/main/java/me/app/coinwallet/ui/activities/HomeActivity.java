package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.exceptions.MnemonicInaccessibleException;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.CustomDialog;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.ToastUtil;
import me.app.coinwallet.viewmodels.HomePageViewModel;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.adapters.TxHistoryAdapter;
import me.app.coinwallet.viewmodels.factory.BiometricViewModelFactory;
import org.bitcoinj.core.Transaction;

public class HomeActivity extends BaseActivity implements BaseAdapter.OnItemClickListener<Transaction> {

    private TextView balance;
    private Button sendButton;
    private Button utxoButton;
    private Button extractMnemonicBtn;
    private Button marketCapBtn;
    private Button encryptBtn;
    private Button encryptCheckBtn;
    private TextView address;
    private HomePageViewModel viewModel;
    private ToastUtil toastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BiometricUtil biometricUtil = new BiometricUtil(this);
        toastUtil = new ToastUtil(this);
        viewModel = new ViewModelProvider(this, new BiometricViewModelFactory(getApplication(), biometricUtil))
                .get(HomePageViewModel.class);
        balance = findViewById(R.id.balance);
        sendButton = findViewById(R.id.send_page_button);
        utxoButton = findViewById(R.id.utxo_button);
        marketCapBtn = findViewById(R.id.market_cap_button);
        encryptBtn = findViewById(R.id.encrypt_button);
        encryptCheckBtn = findViewById(R.id.encrypt_check_button);
        address = findViewById(R.id.wallet_key);
        RecyclerView history = findViewById(R.id.tx_history);
        extractMnemonicBtn = findViewById(R.id.extract_mnemonic_button);
        history.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        TxHistoryAdapter historyAdapter = new TxHistoryAdapter(this);
        history.setAdapter(historyAdapter);
        sendButton.setOnClickListener(v -> moveTo(TransferActivity.class));
        utxoButton.setOnClickListener(v-> viewModel.checkUtxo());
        extractMnemonicBtn.setOnClickListener(v-> {
            try{
                viewModel.extractMnemonic();
            } catch (MnemonicInaccessibleException e){
                Log.e("HD","Mnemonic is not accessible");
                toastUtil.postToast(e.getMessage(),Toast.LENGTH_SHORT);
            }
        });
        encryptCheckBtn.setOnClickListener(v->viewModel.encryptCheck());
        viewModel.getEncryptBtnLabel().observe(this, s->encryptBtn.setText(s));
        encryptBtn.setOnClickListener(v -> showEncryptPasswordDialog());
        marketCapBtn.setOnClickListener(v -> moveTo(MarketCapActivity.class));
        viewModel.getBalance().observe(this, s->balance.setText(s));
        viewModel.getAddress().observe(this, s->address.setText(s));
        viewModel.getHistory().observe(this, historyAdapter::update);
    }

    private void showEncryptPasswordDialog(){
        ConfirmDialog dialog = CustomDialog.passwordDialog(getLayoutInflater(),
                (password)->viewModel.encryptOrDecrypt(password));
        dialog.show(getSupportFragmentManager(),"encrypt_password_dialog");
    }

    private void moveTo(Class<?> dest){
        Intent intent = new Intent(this, dest);
        startActivity(intent);
    }

    @Override
    public void onClick(Transaction item) {

    }
}
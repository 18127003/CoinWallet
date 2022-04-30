package me.app.coinwallet.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.viewmodels.HomePageViewModel;
import me.app.coinwallet.R;
import me.app.coinwallet.adapters.TxHistoryAdapter;

public class HomeActivity extends BaseActivity {

    private TextView balance;
    private Button sendButton;
    private Button utxoButton;
    private Button extractMnemonicBtn;
    private Button marketCapBtn;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final HomePageViewModel viewModel = new ViewModelProvider(this)
                .get(HomePageViewModel.class);
        balance = findViewById(R.id.balance);
        sendButton = findViewById(R.id.send_page_button);
        utxoButton = findViewById(R.id.utxo_button);
        marketCapBtn = findViewById(R.id.market_cap_button);
        address = findViewById(R.id.wallet_key);
        RecyclerView history = findViewById(R.id.tx_history);
        extractMnemonicBtn = findViewById(R.id.extract_mnemonic_button);
        history.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        TxHistoryAdapter historyAdapter = new TxHistoryAdapter();
        history.setAdapter(historyAdapter);
        sendButton.setOnClickListener(v -> moveTo(TransferActivity.class));
        utxoButton.setOnClickListener(v-> viewModel.checkUxto());
        extractMnemonicBtn.setOnClickListener(v-> viewModel.extractMnemonic(getApplicationContext().getFilesDir()));
        marketCapBtn.setOnClickListener(v -> moveTo(MarketCapActivity.class));
        viewModel.getBalance().observe(this, s->balance.setText(s));
        viewModel.getAddress().observe(this, s->address.setText(s));
        viewModel.getHistory().observe(this, historyAdapter::updateHistory);
    }

    public void moveTo(Class<?> dest){
        Intent intent = new Intent(this, dest);
        startActivity(intent);
    }
}
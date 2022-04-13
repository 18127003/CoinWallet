package me.app.coinwallet.activities;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.viewmodels.HomePageViewModel;
import me.app.coinwallet.R;
import me.app.coinwallet.adapters.TxHistoryAdapter;

public class HomeActivity extends BaseActivity {

    private TextView balance;
    private Button sendButton;
    private Button utxoButton;
    private TextView address;
    private EditText sendAmount;
    private EditText sendAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final HomePageViewModel viewModel = new ViewModelProvider(this)
                .get(HomePageViewModel.class);
        balance = findViewById(R.id.balance);
        sendButton = findViewById(R.id.send_button);
        utxoButton = findViewById(R.id.utxo_button);
        address = findViewById(R.id.wallet_key);
        sendAmount = findViewById(R.id.send_text);
        sendAddress = findViewById(R.id.send_address_text);
        RecyclerView history = findViewById(R.id.tx_history);
        history.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        TxHistoryAdapter historyAdapter = new TxHistoryAdapter();
        history.setAdapter(historyAdapter);
        address.setText(viewModel.getAddress());
        Log.e("HD",address.getText().toString());
        sendButton.setOnClickListener(v -> viewModel.send(sendAddress.getText().toString(), sendAmount.getText().toString()));
        utxoButton.setOnClickListener(v->viewModel.checkUxto());
        viewModel.getBalance().observe(this, s->balance.setText(s));
        viewModel.getHistory().observe(this, historyAdapter::updateHistory);
    }
}
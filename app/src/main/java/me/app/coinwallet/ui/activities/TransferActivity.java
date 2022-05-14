package me.app.coinwallet.ui.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.fragments.TransferFragment;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

public class TransferActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        TransferPageViewModel viewModel = new ViewModelProvider(this).get(TransferPageViewModel.class);
        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        Intent intent = getIntent();
        String address = intent.getStringExtra(Constants.SEND_TO_ADDRESS_EXTRA_NAME);
        viewModel.setSendToAddress(address);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment(TransferFragment.class);
    }
}
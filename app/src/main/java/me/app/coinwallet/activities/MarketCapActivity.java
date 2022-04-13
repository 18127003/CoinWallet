package me.app.coinwallet.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.adapters.MarketCapAdapter;
import me.app.coinwallet.marketcap.MarketCapEntry;
import me.app.coinwallet.viewmodels.MarketCapViewModel;

import java.util.List;

public class MarketCapActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MarketCapViewModel viewModel;
    private MarketCapAdapter adapter;
    private Configuration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_cap);
        configuration = getWalletApplication().getConfiguration();
        viewModel = new ViewModelProvider(this).get(MarketCapViewModel.class);
        RecyclerView marketCaps = findViewById(R.id.market_caps);
        marketCaps.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MarketCapAdapter();
        marketCaps.setAdapter(adapter);
        viewModel.marketCapData().observe(this, adapter::updateMarketCaps);
        configuration.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e("HD","Preference changed");
        List<MarketCapEntry> data = viewModel.marketCapData().getValue();
        adapter.updateMarketCaps(data);
    }

    @Override
    public void onDestroy() {
        configuration.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
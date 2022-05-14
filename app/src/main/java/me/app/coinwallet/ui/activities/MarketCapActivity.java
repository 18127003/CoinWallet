package me.app.coinwallet.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.MarketCapAdapter;
import me.app.coinwallet.data.marketcap.MarketCapEntry;
import me.app.coinwallet.viewmodels.MarketCapViewModel;

import java.util.List;

public class MarketCapActivity extends BaseActivity implements BaseAdapter.OnItemClickListener<MarketCapEntry> {

    private MarketCapViewModel viewModel;
    private MarketCapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_cap);
        viewModel = new ViewModelProvider(this).get(MarketCapViewModel.class);
        RecyclerView marketCaps = findViewById(R.id.market_caps);
        marketCaps.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MarketCapAdapter(this);
        marketCaps.setAdapter(adapter);
        viewModel.marketCapData().observe(this, adapter::update);
//        configuration.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
//        configuration.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(MarketCapEntry item) {

    }
}
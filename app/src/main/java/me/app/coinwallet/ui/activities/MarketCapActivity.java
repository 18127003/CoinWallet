package me.app.coinwallet.ui.activities;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.MarketCapAdapter;
import me.app.coinwallet.data.marketcap.MarketCapEntry;
import me.app.coinwallet.viewmodels.MarketCapViewModel;

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
        adapter = new MarketCapAdapter(this,getResources());
        marketCaps.setAdapter(adapter);
        viewModel.marketCapData().observe(this, (v)->{
            adapter.update(v);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(MarketCapEntry item) {

    }
}
package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntry;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.MarketCapAdapter;
import me.app.coinwallet.viewmodels.MarketCapViewModel;

public class MarketCapFragment extends Fragment implements BaseAdapter.OnItemClickListener<MarketCapEntry> {

    MarketCapViewModel viewModel;

    public MarketCapFragment() {
        // Required empty public constructor
    }

    public static MarketCapFragment newInstance() {

        return new MarketCapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market_cap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MarketCapViewModel.class);
        RecyclerView marketCaps = view.findViewById(R.id.market_caps);
        marketCaps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MarketCapAdapter adapter = new MarketCapAdapter(this,getResources());
        marketCaps.setAdapter(adapter);
        viewModel.marketCapData().observe(this, adapter::update);

    }

    @Override
    public void onClick(MarketCapEntry item) {
        Intent i= new Intent(requireContext(), SingleFragmentActivity.class);
        i.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME,ChartDetail.class);
        i.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME,"Chart Detail");
        i.putExtra("chart_detail", item);
        startActivity(i);
    }
}
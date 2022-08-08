package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.MarketCapAdapter;
import me.app.coinwallet.viewmodels.MarketCapViewModel;

public class MarketCapFragment extends Fragment {

    MarketCapViewModel viewModel;
    ShimmerFrameLayout placeholder;

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
        placeholder = view.findViewById(R.id.market_cap_placeholder);
        RecyclerView marketCaps = view.findViewById(R.id.market_caps);
        marketCaps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MarketCapAdapter adapter = new MarketCapAdapter(item -> {
            Intent i= SingleFragmentActivity.newActivity(requireContext(), MarketCapDetailFragment.class,
                    getString(R.string.market_cap_detail_page_label, item.getName()));
            i.putExtra("chart_detail", item);
            startActivity(i);
        }, getResources());
        marketCaps.setAdapter(adapter);
        viewModel.marketCapData().observe(this, l->{
            if(l.size() > 0){
                placeholder.stopShimmerAnimation();
                placeholder.setVisibility(View.GONE);
            }
            marketCaps.setVisibility(View.VISIBLE);
            adapter.update(l);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        placeholder.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        placeholder.stopShimmerAnimation();
    }
}
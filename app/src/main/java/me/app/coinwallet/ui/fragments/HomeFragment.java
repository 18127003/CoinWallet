package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.data.Entry;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntry;
import me.app.coinwallet.ui.activities.MarketCapActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.MarketCapTrendAdapter;
import me.app.coinwallet.viewmodels.HomePageViewModel;

public class HomeFragment extends Fragment {

    TextView balance;
    TextView invisibleText;
    ImageButton visible;
    HomePageViewModel viewModel;
    Button sendBtn;
    Button bluetoothBtn;
    Button requestBtn;

    RecyclerView marketCaps;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomePageViewModel.class);
        balance = view.findViewById(R.id.balance_text);
        invisibleText = view.findViewById(R.id.invisible);
        visible=view.findViewById(R.id.invisible_button);
        sendBtn = view.findViewById(R.id.send_button);
        visible.setBackgroundResource(R.drawable.ic_visibility_off);
        visible.setOnClickListener(v-> hideOrShow());
        viewModel.getBalance().observe(this, s -> balance.setText(s));
        sendBtn.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
            intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, ExchangeRateFragment.class);
            intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Exchange rate");
            startActivity(intent);
        });

        bluetoothBtn = view.findViewById(R.id.bt_text_button);
        bluetoothBtn.setOnClickListener(v->{

        });

        requestBtn = view.findViewById(R.id.request_button);
        requestBtn.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
            intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, PaymentRequestFragment.class);
            intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Payment Request");
            startActivity(intent);
        });

        ImageButton coinMarketBtn=view.findViewById(R.id.coin_market_btn);
        coinMarketBtn.setOnClickListener(v ->{
            Intent intent = new Intent(requireActivity(), SingleFragmentActivity.class);
            intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Market Cap");
            intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, MarketCapFragment.class);
            startActivity(intent);
        });
        marketCaps = view.findViewById(R.id.market_list);
        MarketCapTrendAdapter adapter = new MarketCapTrendAdapter(item -> {
            Intent i= new Intent(requireContext(), SingleFragmentActivity.class);
            i.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME,ChartDetail.class);
            i.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME,"Chart Detail");
            i.putExtra("chart_detail", item);
            startActivity(i);
        });
        marketCaps.setAdapter(adapter);
        marketCaps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        viewModel.getTrendLiveData().observe(this,adapter::update);
    }

    private void hideOrShow() {
        if (balance.getVisibility() == View.INVISIBLE){
            balance.setVisibility(View.VISIBLE);
            invisibleText.setVisibility(View.INVISIBLE);
            visible.setBackgroundResource(R.drawable.ic_visibility);

        } else {
            balance.setVisibility(View.INVISIBLE);
            invisibleText.setVisibility(View.VISIBLE);
            visible.setBackgroundResource(R.drawable.ic_visibility_off);
        }
    }
}
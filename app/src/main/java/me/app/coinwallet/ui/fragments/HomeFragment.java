package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.MarketCapTrendAdapter;
import me.app.coinwallet.utils.Utils;
import me.app.coinwallet.viewmodels.HomePageViewModel;
import org.bitcoinj.core.TransactionConfidence;

public class HomeFragment extends Fragment {

    TextView balance;
    TextView invisibleText;
    ImageButton visible;
    HomePageViewModel viewModel;
    Button exchangeRatesBtn;
    Button requestBtn;
    MaterialCardView transactionCard;
    TextView receiver;
    TextView time;
    TextView amount;
    TextView confirmNum;
    ImageView status;
    View txView;
    ShimmerFrameLayout placeholder;
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
        visible = view.findViewById(R.id.invisible_button);
        placeholder = view.findViewById(R.id.trend_placeholder);
        visible.setBackgroundResource(R.drawable.ic_visibility_off);
        visible.setOnClickListener(v-> hideOrShow());
        viewModel.getBalance().observe(this, s -> balance.setText(s));

        exchangeRatesBtn = view.findViewById(R.id.exchange_rate_btn);
        exchangeRatesBtn.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
            intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, ExchangeRateFragment.class);
            intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Exchange rate");
            startActivity(intent);
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
            i.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, MarketCapDetailFragment.class);
            i.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME,"Chart Detail");
            i.putExtra("chart_detail", item);
            startActivity(i);
        }, getResources());
        marketCaps.setAdapter(adapter);
        marketCaps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        viewModel.getTrendLiveData().observe(this,l->{
            placeholder.stopShimmerAnimation();
            placeholder.setVisibility(View.GONE);
            marketCaps.setVisibility(View.VISIBLE);
            adapter.update(l);
        });
        txView = view.findViewById(R.id.card_latest_transaction).findViewById(R.id.transaction_item);
        transactionCard = txView.findViewById(R.id.tx_item_card);
        receiver = txView.findViewById(R.id.tx_receiver);
        time = txView.findViewById(R.id.tx_time);
        amount = txView.findViewById(R.id.tx_amount);
        confirmNum = txView.findViewById(R.id.tx_confirmation_number);
        status = txView.findViewById(R.id.tx_status);
        viewModel.getLatestTx().observe(this, this::renderLastTx);

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

    private void renderLastTx(TransactionWrapper tx){
        if(tx==null){
            return;
        }
        receiver.setText(tx.getReceiver().toString());
        time.setText(Utils.formatDate(tx.getTime()));
        confirmNum.setText(getResources().getText(R.string.confirmation)+": "+tx.getConfirmNum().toString());
        amount.setText(tx.getAmount().toFriendlyString());
        TransactionConfidence.ConfidenceType type = tx.getStatus();
        switch (type){
            case DEAD:
                status.setBackgroundResource(R.drawable.ic_baseline_cancel_24);
                amount.setTextColor(getResources().getColor(R.color.red));
                break;
            case PENDING:
                status.setBackgroundResource(R.drawable.ic_pending);
                amount.setTextColor(getResources().getColor(R.color.grey));
                break;
            case BUILDING:
                status.setBackgroundResource(R.drawable.ic_done);
                amount.setTextColor(getResources().getColor(R.color.light_green));
        }
        txView.setOnClickListener(v-> {
            Intent intent= new Intent(requireContext(), SingleFragmentActivity.class);
            intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME,"Transaction Detail");
            intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME,TransactionDetailFragment.class);
            intent.putExtra("transaction",tx);
            startActivity(intent);
        });
    }
}
package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.color.MaterialColors;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntity;
import me.app.coinwallet.utils.CandleStickChartUtil;
import me.app.coinwallet.utils.ChartUtil;

public class MarketCapDetailFragment extends Fragment {
    private MarketCapEntity marketCapEntity;
    CandleStickChart chart;

    public MarketCapDetailFragment(){}
    public static MarketCapDetailFragment newInstance() {
        return new MarketCapDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Intent intent= requireActivity().getIntent();
        marketCapEntity = intent.getParcelableExtra("chart_detail");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market_cap_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = view.findViewById(R.id.chart);
        new CandleStickChartUtil(marketCapEntity, getResources()).chart(chart)
                .description(MaterialColors.getColor(view,R.attr.colorOnSecondary))
                .visualize(true,MaterialColors.getColor(view,R.attr.colorOnError));

        TextView price=view.findViewById(R.id.price);
        TextView marketCapDominationIndex=view.findViewById(R.id.market_cap_domination_index);
        TextView marketCapitalization=view.findViewById(R.id.market_capitalization);
        TextView volumeMarketCap=view.findViewById(R.id.volume_marketCap);
        TextView highLow=view.findViewById(R.id.low_high_24h);
        TextView rank=view.findViewById(R.id.ranking);

        price.setText(""+ marketCapEntity.getCurrentPrice()+"$");
        highLow.setText(""+ marketCapEntity.getLow()+"/"+ marketCapEntity.getHigh());
        marketCapDominationIndex.setText(""+ marketCapEntity.getFluctuation());
        marketCapitalization.setText(""+ marketCapEntity.getMarketCap());
        rank.setText(""+ marketCapEntity.getMarketCapRank());
        volumeMarketCap.setText(""+ marketCapEntity.getTotalVolume());
    }


}
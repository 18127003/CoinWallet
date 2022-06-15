package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntry;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartDetail extends Fragment {
    private MarketCapEntry marketCapEntry;

    public ChartDetail(){}
    public static ChartDetail newInstance() {
        ChartDetail fragment = new ChartDetail();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chart_detail, container, false);
        Intent intent= requireActivity().getIntent();
        marketCapEntry= intent.getParcelableExtra("chart_detail");

        LineChart chart= view.findViewById(R.id.chart);
        chart.setBackgroundColor(getResources().getColor(R.color.zxing_transparent));
        Description description=new Description();
        description.setText(marketCapEntry.getName());
        description.setTextSize(16);
        description.setTextColor(getResources().getColor(R.color.white));
        chart.setDescription(description);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getLegend().setEnabled(false);
        LineDataSet lineDataSet=new LineDataSet(marketCapEntry.getChart(), marketCapEntry.getName());
        lineDataSet.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet.setDrawCircles(false);
//        lineDataSet.setDrawValues(false);
        if(marketCapEntry.getFluctuation()>=0){
            lineDataSet.setColor(getResources().getColor(R.color.light_green));
        }
        else {
            lineDataSet.setColor(getResources().getColor(R.color.red));
        }
        chart.setData(new LineData(lineDataSet));
        chart.setDragEnabled(false);

        TextView name=view.findViewById(R.id.name);
        TextView price=view.findViewById(R.id.price);
        TextView marketCapDominationIndex=view.findViewById(R.id.market_cap_domination_index);
        TextView marketCapitalization=view.findViewById(R.id.market_capitalization);
        TextView tradingVolume=view.findViewById(R.id.trading_volume);
        TextView volumeMarketCap=view.findViewById(R.id.volume_marketCap);
        TextView highLow=view.findViewById(R.id.low_high_24h);
        TextView rank=view.findViewById(R.id.ranking);

        name.setText("");
        price.setText(""+marketCapEntry.getCurrentPrice());
        highLow.setText(""+marketCapEntry.getLow()+"/"+marketCapEntry.getHigh());
        tradingVolume.setText(""+marketCapEntry.getTotalVolume());
        marketCapDominationIndex.setText(""+marketCapEntry.getFluctuation());
        marketCapitalization.setText(""+marketCapEntry.getMarketCap());
        rank.setText(""+marketCapEntry.getMarketCapRank());
        volumeMarketCap.setText(""+marketCapEntry.getTotalVolume());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
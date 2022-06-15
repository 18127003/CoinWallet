package me.app.coinwallet.ui.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Picasso;
import me.app.coinwallet.R;

import me.app.coinwallet.data.marketcap.MarketCapEntry;

import java.util.ArrayList;
import java.util.List;

public class MarketCapAdapter extends BaseAdapter<MarketCapEntry, MarketCapAdapter.ViewHolder> {

    Resources res;
    public MarketCapAdapter(OnItemClickListener<MarketCapEntry> listener, Resources res){
        super(listener);
        this.res=res;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_cap_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketCapEntry entry = data.get(position);
        View view=holder.itemView;
        CardView detail=view.findViewById(R.id.detail);
        detail.setOnClickListener(v -> listener.onClick(entry));
        ((TextView)view.findViewById(R.id.symbol)).setText(entry.getSymbol());
        ((TextView)view.findViewById(R.id.name)).setText(entry.getName());
        ((TextView)view.findViewById(R.id.rank)).setText("#"+entry.getMarketCapRank());
        TextView fluctuation=((TextView)view.findViewById(R.id.fluctuation));
        fluctuation.setText(""+entry.getFluctuation());
        Picasso.get().load(entry.getImage())
                .resize(75,75).into((ImageView) view.findViewById(R.id.image));
        LineChart chart= holder.itemView.findViewById(R.id.chart);
        chart.setBackgroundColor(holder.itemView.getResources().getColor(R.color.zxing_transparent));
        Description description=new Description();
//        description.setText(entry.getChart().);
        description.setEnabled(false);
        chart.setDescription(description);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        LineDataSet lineDataSet=new LineDataSet(entry.getChart(),entry.getName());
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextColor(holder.itemView.getResources().getColor(R.color.blue_black));
        if(entry.getFluctuation()>=0){
            fluctuation.setTextColor(Color.GREEN);
            lineDataSet.setColor(holder.itemView.getResources().getColor(R.color.light_green));
        }
        else {
            lineDataSet.setColor(holder.itemView.getResources().getColor(R.color.red));
            fluctuation.setTextColor(Color.RED);
        }

        chart.setData(new LineData(lineDataSet));
        chart.setClickable(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);

        chart.fitScreen();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

        }
    }
}

package me.app.coinwallet.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import me.app.coinwallet.R;

import me.app.coinwallet.data.marketcap.MarketCapEntry;
import me.app.coinwallet.viewmodels.HomePageViewModel;

import java.util.List;

public class MarketCapTrendAdapter extends BaseAdapter<MarketCapEntry, MarketCapTrendAdapter.ViewHolder>{

    public MarketCapTrendAdapter(OnItemClickListener<MarketCapEntry> listener) {
        super(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_cap_trend_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketCapEntry chartEntry = data.get(position);

        holder.itemView.findViewById(R.id.trend_card).setOnClickListener(view -> {
            listener.onClick(chartEntry);
        });
        holder.chart.setBackgroundColor(holder.itemView.getResources().getColor(R.color.zxing_transparent));
        Description description=new Description();
        description.setText(chartEntry.getName());
        holder.chart.setDescription(description);
        holder.chart.getAxisRight().setDrawAxisLine(false);
        holder.chart.getAxisRight().setDrawLabels(false);
        holder.chart.getXAxis().setDrawLabels(false);
        holder.chart.getXAxis().setDrawAxisLine(false);
        holder.chart.getLegend().setEnabled(false);
        LineDataSet lineDataSet=new LineDataSet(chartEntry.getChart(), chartEntry.getName());
        lineDataSet.setValueTextColor(holder.itemView.getResources().getColor(R.color.blue_black));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        if(chartEntry.getFluctuation()>=0){
            lineDataSet.setColor(holder.itemView.getResources().getColor(R.color.light_green));
        }
        else {
            lineDataSet.setColor(holder.itemView.getResources().getColor(R.color.red));
        }
        holder.chart.setData(new LineData(lineDataSet));
        holder.chart.setDragEnabled(false);
        holder.chart.setTouchEnabled(false);
        holder.chart.setClickable(false);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LineChart chart;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            chart=view.findViewById(R.id.chart);
        }
    }
}

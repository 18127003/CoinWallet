package me.app.coinwallet.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.ChartEntry;
import me.app.coinwallet.viewmodels.HomePageViewModel;

public class MarketCapTrendAdapter extends BaseAdapter<ChartEntry, MarketCapTrendAdapter.ViewHolder>{
    HomePageViewModel viewModel;
    public MarketCapTrendAdapter(OnItemClickListener<ChartEntry> listener) {
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
        ChartEntry chartEntry = data.get(position);
        holder.chart.setBackgroundColor(holder.itemView.getResources().getColor(R.color.zxing_transparent));
        Description description=new Description();
        description.setText(chartEntry.getId());
        holder.chart.setDescription(description);
        holder.chart.getAxisRight().setDrawAxisLine(false);
        holder.chart.getAxisRight().setDrawLabels(false);
        holder.chart.getLegend().setEnabled(false);
        LineDataSet lineDataSet=new LineDataSet(chartEntry.getPointList(),chartEntry.getId());
        lineDataSet.setValueTextColor(holder.itemView.getResources().getColor(R.color.blue_black));
        if(chartEntry.getPointList().get(chartEntry.getPointList().size()-1).getY()-chartEntry.getPointList().get(0).getY()>=0){
            lineDataSet.setColor(holder.itemView.getResources().getColor(R.color.purple_200));
        }
        else {
            lineDataSet.setColor(holder.itemView.getResources().getColor(R.color.red));
        }
        holder.chart.setData(new LineData(lineDataSet));

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

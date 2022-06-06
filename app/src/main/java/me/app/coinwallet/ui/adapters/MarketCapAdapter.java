package me.app.coinwallet.ui.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.ChartEntry;
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
        ((TextView)view.findViewById(R.id.balance)).setText(""+entry.getCurrentPrice());
        TextView fluctuation=((TextView)view.findViewById(R.id.fluctuation));
        fluctuation.setText(""+entry.getFluctuation());
        if(entry.getFluctuation()>=0){
            fluctuation.setTextColor(Color.GREEN);
        }
        else {
            fluctuation.setTextColor(Color.RED);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

        }
    }
}

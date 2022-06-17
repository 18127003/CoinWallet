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
import me.app.coinwallet.utils.ChartUtil;
import org.w3c.dom.Text;


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
        holder.card.setOnClickListener(v -> listener.onClick(entry));
        holder.symbol.setText(entry.getSymbol());
        holder.name.setText(entry.getName());
        holder.rank.setText("#"+entry.getMarketCapRank());
        holder.fluctuation.setText(""+entry.getFluctuation());
        holder.fluctuation.setTextColor(entry.getFluctuation()>=0?Color.GREEN:Color.RED);
        holder.price.setText("$"+entry.getCurrentPrice());
        Picasso.get().load(entry.getImage())
                .resize(75,75).into(holder.image);
        new ChartUtil(entry, res).chart(holder.chart).disableTouch().disableDescription().disableAxis().visualize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView symbol;
        public TextView name;
        public TextView rank;
        public TextView fluctuation;
        public ImageView image;
        public CardView card;
        public LineChart chart;
        public TextView price;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            card = view.findViewById(R.id.detail);
            symbol = view.findViewById(R.id.symbol);
            name = view.findViewById(R.id.name);
            rank = view.findViewById(R.id.rank);
            fluctuation = view.findViewById(R.id.fluctuation);
            image = view.findViewById(R.id.image);
            chart = view.findViewById(R.id.chart);
            price = view.findViewById(R.id.price);
        }
    }
}

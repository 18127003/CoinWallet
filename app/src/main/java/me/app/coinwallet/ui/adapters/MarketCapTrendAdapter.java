package me.app.coinwallet.ui.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import me.app.coinwallet.R;

import me.app.coinwallet.data.marketcap.MarketCapEntity;
import me.app.coinwallet.utils.CandleStickChartUtil;

public class MarketCapTrendAdapter extends BaseAdapter<MarketCapEntity, MarketCapTrendAdapter.ViewHolder>{

    private final Resources res;
    public MarketCapTrendAdapter(OnItemClickListener<MarketCapEntity> listener, Resources res) {
        super(listener);
        this.res = res;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_cap_trend_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketCapEntity cap = data.get(position);

        holder.trendCard.setOnClickListener(view -> listener.onClick(cap));
        new CandleStickChartUtil(cap,res).chart(holder.chart)
                .disableTouch()
                .disableAxis()
                .description(MaterialColors.getColor(holder.itemView,R.attr.colorOnSecondary))
                .visualize(false,MaterialColors.getColor(holder.itemView,R.attr.colorOnError));

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CandleStickChart chart;
        public MaterialCardView trendCard;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            chart = view.findViewById(R.id.chart);
            trendCard = view.findViewById(R.id.trend_card);
        }
    }
}

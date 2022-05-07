package me.app.coinwallet.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntry;

import java.util.ArrayList;
import java.util.List;

public class MarketCapAdapter extends BaseAdapter<MarketCapEntry, MarketCapAdapter.ViewHolder> {

    public MarketCapAdapter(OnItemClickListener<MarketCapEntry> listener){
        super(listener);
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
        holder.getCurrentPrice().setText(String.valueOf(entry.getCurrentPrice()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView currentPrice;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            currentPrice = view.findViewById(R.id.market_cap_current_price);
        }

        public TextView getCurrentPrice() {
            return currentPrice;
        }
    }
}

package me.app.coinwallet.ui.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.language.LanguageOption;

public class LanguageOptionAdapter extends BaseAdapter<LanguageOption, LanguageOptionAdapter.ViewHolder> {
    private Resources res;
    public LanguageOptionAdapter(OnItemClickListener<LanguageOption> listener, String selected) {
        super(listener);
        this.selected = selected;
    }
    private final String selected;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_option_item,parent,false);
        res=parent.getResources();
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LanguageOption language = data.get(position);
        holder.label.setText(language.getLabel());
        if(language.getCode().equals(selected)){
            holder.label.setTextColor(res.getColor(R.color.yellow));
        }
        holder.card.setOnClickListener(
                v -> {
                    listener.onClick(language);
                    holder.label.setTextColor(res.getColor(R.color.yellow));
                }
        );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView label;
        final MaterialCardView card;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            label = view.findViewById(R.id.language_option_label);
            card = view.findViewById(R.id.language_option_card);
        }
    }
}

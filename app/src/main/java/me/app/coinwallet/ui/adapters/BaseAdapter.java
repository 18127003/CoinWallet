package me.app.coinwallet.ui.adapters;

import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, N extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<N>  {
    protected List<T> data;
    protected final OnItemClickListener<T> listener;

    public BaseAdapter(OnItemClickListener<T> listener){
        data = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public N onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull N holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(List<T> newData){
        data.clear();
        data = newData;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<T>{
        void onClick(T item);
    }
}

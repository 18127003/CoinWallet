package me.app.coinwallet.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.transaction.MonthlyReport;

public class TxHistoryAdapter extends BaseAdapter<MonthlyReport, TxHistoryAdapter.ViewHolder> {

    /***
     * Self-context
     */
    private Context context;
    private final RecyclerView.RecycledViewPool viewPool;

    public TxHistoryAdapter(){
        super(null);
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View rootView = LayoutInflater.from(context).inflate(R.layout.month_tx_body,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonthlyReport monthlyReport = data.get(position);
        TransactionAdapter adapter = new TransactionAdapter();
        adapter.update(monthlyReport.getTransactions());
        holder.transactions.setAdapter(adapter);
        holder.transactions.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.transactions.setRecycledViewPool(viewPool);
//        monthlyReport.getTransactions().observe(this, adapter::update);
        holder.income.setText(monthlyReport.sumIncome());
        holder.outcome.setText(monthlyReport.sumOutcome());
        holder.time.setText(monthlyReport.getTime());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView transactions;
        private final TextView income;
        private final TextView time;
        private final TextView outcome;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            transactions = view.findViewById(R.id.month_tx_list);
            income = view.findViewById(R.id.report_income);
            outcome = view.findViewById(R.id.report_outcome);
            time = view.findViewById(R.id.report_time);
        }
    }

}

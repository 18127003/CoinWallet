package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.adapters.TxHistoryAdapter;
import me.app.coinwallet.viewmodels.HomePageViewModel;

public class HistoryFragment extends Fragment implements BaseAdapter.OnItemClickListener<TransactionWrapper> {

    private HomePageViewModel viewModel;
    private RecyclerView history;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomePageViewModel.class);
        history = view.findViewById(R.id.history_list);
        TxHistoryAdapter adapter = new TxHistoryAdapter(this);
        history.setAdapter(adapter);
        history.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.getMonthlyReports().observe(this, adapter::update);
    }

    @Override
    public void onClick(TransactionWrapper item) {
        Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
        intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, TransactionDetailFragment.class);
        intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Transaction Detail");
        intent.putExtra("transaction",item);
        startActivity(intent);
    }
}
package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.ColorRes;
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

public class HistoryFragment extends Fragment {

    private HomePageViewModel viewModel;
    private RecyclerView history;
    private Button success;
    private Button fail;
    private Button pending;
    private Button all;

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
        success = view.findViewById(R.id.success);
        pending = view.findViewById(R.id.pending);
        fail = view.findViewById(R.id.fail);
        all = view.findViewById(R.id.all);
        TxHistoryAdapter adapter = new TxHistoryAdapter(item -> {
            Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
            intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, TransactionDetailFragment.class);
            intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Transaction Detail");
            intent.putExtra("transaction",item);
            startActivity(intent);
        });
        history.setAdapter(adapter);
        history.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.getMonthlyReports().observe(this, adapter::update);
        success.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.SUCCESS));
        all.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.ALL));
        fail.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.FAIL));
        pending.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.PENDING));
    }

    private void selectFilter(HomePageViewModel.Filter filter){
        int selected = R.color.gold;
        int remain = R.color.blue_black;
        switch (filter){
            case ALL:
                colorFilter(remain, remain, remain, selected);
                break;
            case FAIL:
                colorFilter(remain,selected,remain,remain);
                break;
            case PENDING:
                colorFilter(remain,remain,selected,remain);
                break;
            case SUCCESS:
                colorFilter(selected,remain,remain,remain);
                break;
        }
        viewModel.filter(filter);
    }

    private void colorFilter(@ColorRes int success, @ColorRes int fail, @ColorRes int pending, @ColorRes int all){
        Resources res = getResources();
        this.success.setTextColor(res.getColor(success));
        this.fail.setTextColor(res.getColor(fail));
        this.pending.setTextColor(res.getColor(pending));
        this.all.setTextColor(res.getColor(all));
    }
}
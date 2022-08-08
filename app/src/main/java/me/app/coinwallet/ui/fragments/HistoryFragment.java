package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.color.MaterialColors;
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
    private int selectedColor;
    private int remainColor;

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
        }, getResources());
        history.setAdapter(adapter);
        history.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.getMonthlyReports().observe(this, adapter::update);
        selectedColor = MaterialColors.getColor(view, R.attr.colorSecondaryVariant);
        remainColor = MaterialColors.getColor(view, R.attr.colorSecondary);
        success.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.SUCCESS));
        all.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.ALL));
        fail.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.FAIL));
        pending.setOnClickListener(v->selectFilter(HomePageViewModel.Filter.PENDING));
    }

    private void selectFilter(HomePageViewModel.Filter filter){
        switch (filter){
            case ALL:
                colorFilter(remainColor, remainColor, remainColor, selectedColor);
                break;
            case FAIL:
                colorFilter(remainColor,selectedColor,remainColor,remainColor);
                break;
            case PENDING:
                colorFilter(remainColor,remainColor,selectedColor,remainColor);
                break;
            case SUCCESS:
                colorFilter(selectedColor,remainColor,remainColor,remainColor);
                break;
        }
        viewModel.filter(filter);
    }

    private void colorFilter(@ColorInt int success, @ColorInt int fail, @ColorInt int pending, @ColorInt int all){
        Resources res = getResources();
        this.success.setTextColor(success);
        this.fail.setTextColor(fail);
        this.pending.setTextColor(pending);
        this.all.setTextColor(all);
    }
}
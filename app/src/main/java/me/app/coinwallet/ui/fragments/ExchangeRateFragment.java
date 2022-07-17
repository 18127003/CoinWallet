package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.listeners.RepositoryQueryListener;
import me.app.coinwallet.viewmodels.ExchangeRatesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExchangeRateFragment extends Fragment {
    private ExchangeRatesViewModel exchangeRatesViewModel;
    private CircularProgressIndicator indicator;

    public ExchangeRateFragment() {
        // Required empty public constructor
    }

    public static ExchangeRateFragment newInstance() {
        return new ExchangeRateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exchange_rate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        indicator = view.findViewById(R.id.progress_circular);
        exchangeRatesViewModel =new ViewModelProvider(requireActivity()).get(ExchangeRatesViewModel.class);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.coin_select_item,
                new ArrayList<>());
        exchangeRatesViewModel.getNameList().observe(this, (s)->{
            adapter.clear();
            adapter.addAll(s);
            adapter.notifyDataSetChanged();
        });
        AutoCompleteTextView coinTo = view.findViewById(R.id.card_to).findViewById(R.id.coin);
        coinTo.setAdapter(adapter);

        TextInputEditText textInputEditText= view.findViewById(R.id.currency);
        Button button= view.findViewById(R.id.button);
        TextView result= view.findViewById(R.id.result);
        exchangeRatesViewModel.getExchangeRate().observe(this,(s)->{
            result.setText("Name: " +s.nameCoin+"\nRate: "+s.valueCurrency.toString()+s.unit);
            if(!textInputEditText.getText().toString().isEmpty()){
                result.append("\n"+ textInputEditText.getText()+"BTC = "
                        +exchangeRatesViewModel
                        .calculate(Float.valueOf(String.valueOf(textInputEditText.getText())),s.valueCurrency)
                        .toPlainString()+s.unit);
            }
        });
        button.setOnClickListener(v->{
            result.setText("");
            exchangeRatesViewModel.queryExchangeRates(coinTo.getText().toString());
            coinTo.clearFocus();
        });
        exchangeRatesViewModel.isQuerying().observe(this, isQuerying->{
            if(isQuerying){
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setVisibility(View.GONE);
            }
        });
    }
}
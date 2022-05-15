package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.app.coinwallet.R;

import java.util.Arrays;
import java.util.List;

public class ExchangeRateFragment extends Fragment {

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
        List<String> items = Arrays.asList("dfjds0", "dsjfsjd");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.coin_select_item, items);
        AutoCompleteTextView coinFrom = view.findViewById(R.id.card_from).findViewById(R.id.coin);
        coinFrom.setAdapter(adapter);
        AutoCompleteTextView coinTo = view.findViewById(R.id.card_to).findViewById(R.id.coin);
        coinTo.setAdapter(adapter);
    }
}
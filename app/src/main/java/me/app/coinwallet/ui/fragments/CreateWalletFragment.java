package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;

import java.util.Arrays;
import java.util.List;

public class CreateWalletFragment extends Fragment {

    private TextInputEditText label;
    private Button createBtn;

    public CreateWalletFragment() {
        // Required empty public constructor
    }

    public static CreateWalletFragment newInstance() {
        return new CreateWalletFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        label = view.findViewById(R.id.label_text_field);

        List<String> items = Arrays.asList(new String[]{"Bitcoin Mainnet", "Bitcoin Testnet", "Linecoin Testnet"});
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.coin_select_item, items);
        AutoCompleteTextView option= view.findViewById(R.id.select_option);
        option.setAdapter(adapter);

        createBtn = view.findViewById(R.id.create_wallet_btn);
        createBtn.setOnClickListener(v->{
            Intent result = new Intent();
            result.putExtra(Constants.WALLET_LABEL_EXTRA_NAME, label.getText().toString());
            requireActivity().setResult(Activity.RESULT_OK, result);
            requireActivity().finish();
        });
    }
}
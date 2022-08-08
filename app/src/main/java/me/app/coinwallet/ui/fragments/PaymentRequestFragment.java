package me.app.coinwallet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.viewmodels.PaymentRequestViewModel;

public class PaymentRequestFragment extends Fragment {

    private TextInputEditText amount;
    private SwitchMaterial bluetoothSwitch;
    private Button generateBtn;
    private PaymentRequestViewModel viewModel;

    public PaymentRequestFragment() {
        // Required empty public constructor
    }

    public static PaymentRequestFragment newInstance() {
        return new PaymentRequestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        amount = view.findViewById(R.id.amount_text_field);
        bluetoothSwitch = view.findViewById(R.id.bluetooth_switch);
        generateBtn = view.findViewById(R.id.generate_qr_button);
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentRequestViewModel.class);
        viewModel.getUri().observe(requireActivity(), s -> {
            Intent intent = SingleFragmentActivity.newActivity(requireContext(), PaymentQrFragment.class, R.string.scan_qr_page_label);
            intent.putExtra("uri", s);
            startActivity(intent);
        });
        generateBtn.setOnClickListener(v->{
            try{
                double amountDouble = Double.parseDouble(amount.getText().toString());
                viewModel.generateUri(amountDouble, bluetoothSwitch.isChecked());
            } catch (NullPointerException | NumberFormatException exception){
                // toast
            }
        });
    }

}
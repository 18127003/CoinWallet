package me.app.coinwallet.ui.fragments;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.app.coinwallet.R;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.utils.Utils;

public class TransactionDetailFragment extends Fragment {
    TransactionWrapper transactionWrapper;

    public TransactionDetailFragment() {
        // Required empty public constructor
    }

    public static TransactionDetailFragment newInstance() {
        return new TransactionDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionWrapper=(TransactionWrapper) requireActivity().getIntent().getSerializableExtra("transaction");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView icon=view.findViewById(R.id.tx_in_or_out);
        TextView type=view.findViewById(R.id.type);
        TextView amount=view.findViewById(R.id.amount);
        TextView tx_id=view.findViewById(R.id.tx_id);
        AppCompatButton status=view.findViewById(R.id.tx_status_text);
        TextView time=view.findViewById(R.id.tx_time);
        TextView from=view.findViewById(R.id.source);
        TextView fee=view.findViewById(R.id.fee);
        TextView conf=view.findViewById(R.id.conf);
        TextView address=view.findViewById(R.id.address);
        amount.setText(transactionWrapper.getAmount().toFriendlyString());
        if(transactionWrapper.isSend()){
            icon.setBackgroundResource(R.drawable.ic_send);
            type.setText(R.string.send_button_label);
            address.setText(transactionWrapper.getReceiver().toString());
        }
        else {
            icon.setBackgroundResource(R.drawable.ic_receive);
            type.setText(R.string.receive);
        }
        switch (transactionWrapper.getStatus()){
            case PENDING:
                status.setText(R.string.pending);
                status.setBackgroundColor(getResources().getColor(R.color.grey));
                status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pending,0,0,0);
                break;
            case DEAD:
                status.setText(R.string.fail);
                status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24,0,0,0);
                status.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case BUILDING:
                status.setText(R.string.success);
                status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_done,0,0,0);
                status.setBackgroundColor(getResources().getColor(R.color.light_green));
                break;
        }
        tx_id.setText(getString(R.string.transaction_id)+": "+transactionWrapper.getTxId().toString());
        time.setText(Utils.formatDate(transactionWrapper.getTime()));
        fee.setText(transactionWrapper.getFee().toFriendlyString());
        conf.setText(transactionWrapper.getConfirmNum().toString());
    }
}
package me.app.coinwallet.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.journeyapps.barcodescanner.*;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.transfer.PaymentRequest;
import org.bitcoinj.uri.BitcoinURIParseException;

public class ScanQrFragment extends Fragment {

    DecoratedBarcodeView barcodeView;
    private boolean askedPermission = false;

    public ScanQrFragment() {
        // Required empty public constructor
    }

    public static ScanQrFragment newInstance() {
        ScanQrFragment fragment = new ScanQrFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barcodeView = view.findViewById(R.id.barcode_view);
        barcodeView.setStatusText("");
        barcodeView.decodeContinuous(result -> {
            Intent intent = new Intent(getContext(), SingleFragmentActivity.class);
            try {
                PaymentRequest paymentRequest = PaymentRequest.from(result.getText());
                intent.putExtra(Constants.QR_CONTENT, paymentRequest);
                intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Transfer Money");
                intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, TransferFragment.class);
                startActivity(intent);
            } catch (BitcoinURIParseException e) {
                // swallow
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(barcodeView != null) {
            if (isVisibleToUser) {
                barcodeView.resume();
            } else {
                barcodeView.pauseAndWait();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        openCameraWithPermission();
    }

    private void openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        } else if (!askedPermission) {
            requestCameraCallback.launch(Manifest.permission.CAMERA);
            askedPermission = true;
        } // else wait for permission result
    }

    private final ActivityResultLauncher<String> requestCameraCallback = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    barcodeView.resume();
                } else {
                    Log.e("HD","request not granted");
                }
            });
}
package me.app.coinwallet.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.journeyapps.barcodescanner.*;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.utils.QRUtil;
import me.app.coinwallet.utils.ToastUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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
        barcodeView.decodeSingle(result -> barcodeView.setStatusText(result.getText()));
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
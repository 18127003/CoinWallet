package me.app.coinwallet.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.color.MaterialColors;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.utils.QRUtil;
import me.app.coinwallet.viewmodels.ScanQrPageViewModel;

public class ShowQrFragment extends Fragment {

    ImageView qrCodeImg;
    TextView address;
    ScanQrPageViewModel viewModel;

    public ShowQrFragment() {
        // Required empty public constructor
    }

    public static ShowQrFragment newInstance() {
        ShowQrFragment fragment = new ShowQrFragment();
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
        return inflater.inflate(R.layout.fragment_show_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ScanQrPageViewModel.class);
        qrCodeImg = view.findViewById(R.id.qr_code_img);
        address = view.findViewById(R.id.address);
        viewModel.getAddress().observe(requireActivity(), s -> {
            Bitmap bm = QRUtil.createQRCodeBitmap(s);
            if (bm != null) {
                qrCodeImg.setBackgroundResource(R.color.white);
                qrCodeImg.setColorFilter(MaterialColors.getColor(view, R.attr.colorOnBackground));
                qrCodeImg.setImageBitmap(Bitmap.createScaledBitmap(bm,
                        Constants.QR_BITMAP_SCALE_WIDTH, Constants.QR_BITMAP_SCALE_HEIGHT, false));
            }
        });
        viewModel.getAddress().observe(this, s->address.setText(s));
    }
}
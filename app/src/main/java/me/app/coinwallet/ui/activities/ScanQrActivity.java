package me.app.coinwallet.ui.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.fragments.ScanQrFragment;
import me.app.coinwallet.ui.fragments.ShowQrFragment;

public class ScanQrActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.scanner:
                    loadFragment(ScanQrFragment.class);
                    break;
                case R.id.qr_code:
                    loadFragment(ShowQrFragment.class);
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment(ScanQrFragment.class);
    }
}
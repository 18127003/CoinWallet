package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import me.app.coinwallet.ui.fragments.HistoryFragment;
import me.app.coinwallet.ui.fragments.HomeFragment;
import me.app.coinwallet.ui.fragments.SettingFragment;
import me.app.coinwallet.ui.fragments.TransferFragment;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.viewmodels.HomePageViewModel;
import me.app.coinwallet.R;

public class HomeActivity extends BaseActivity {

    private FloatingActionButton scanBtn;
    private MaterialToolbar materialToolbar;
    private BottomNavigationView bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        scanBtn = findViewById(R.id.scan_fab);
        scanBtn.setOnClickListener(v -> moveTo(ScanQrActivity.class));

        materialToolbar=findViewById(R.id.top_app_bar);
        setSupportActionBar(materialToolbar);
        bottomAppBar = findViewById(R.id.bottom_navigation);
        bottomAppBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_home:
                    loadFragment(HomeFragment.class);
                    break;
                case R.id.page_history:
                    loadFragment(HistoryFragment.class);
                    break;
                case R.id.page_transfer:
                    loadFragment(TransferFragment.class);
                    break;
                case R.id.page_setting:
                    loadFragment(SettingFragment.class);
                    break;
                default:
                    return false;
            }
            return true;
        });
        loadFragment(HomeFragment.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void moveTo(Class<?> dest){
        Intent intent = new Intent(this, dest);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1){
            super.onBackPressed();
        }
    }
}
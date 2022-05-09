package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.fragments.HomeFragment;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.viewmodels.HomePageViewModel;
import me.app.coinwallet.R;
import me.app.coinwallet.viewmodels.factory.BiometricViewModelFactory;
import org.bitcoinj.core.Transaction;

public class HomeActivity extends BaseActivity implements BaseAdapter.OnItemClickListener<Transaction> {

    private HomePageViewModel viewModel;
    private FloatingActionButton scanBtn;
    private MaterialToolbar materialToolbar;
    private BottomNavigationView bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BiometricUtil biometricUtil = new BiometricUtil(this);
        viewModel = new ViewModelProvider(this, new BiometricViewModelFactory(getApplication(), biometricUtil))
                .get(HomePageViewModel.class);
        scanBtn = findViewById(R.id.scan_fab);
//        scanBtn.setOnClickListener(v -> moveTo(CodeActivity.class));

        materialToolbar=findViewById(R.id.top_app_bar);
        materialToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.notification:
//                    loadFragment(NotificationFragment().newInstance());
                    break;
                case R.id.profile:
//                    loadFragment(PersonalFragment().newInstance());
                    break;
                default:
                    return false;
            }
            return true;
        });

        bottomAppBar = findViewById(R.id.bottom_navigation);
        bottomAppBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_home:
                    loadFragment(HomeFragment.newInstance());
                    break;
                case R.id.page_history:
//                    loadFragment(fragment);
                    break;
                case R.id.page_transfer_rate:
//                    loadFragment(fragment);
                    break;
                case R.id.page_setting:
//                    loadFragment(fragment);
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
        loadFragment(HomeFragment.newInstance());
    }

    private void moveTo(Class<?> dest){
        Intent intent = new Intent(this, dest);
        startActivity(intent);
    }

    @Override
    public void onClick(Transaction item) {

    }
}
package me.app.coinwallet.ui.activities;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.fragments.SelectWalletFragment;
import me.app.coinwallet.viewmodels.InitPageViewModel;

public class InitActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        loadFragment(SelectWalletFragment.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
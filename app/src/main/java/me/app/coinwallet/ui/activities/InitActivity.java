package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.adapters.WalletInfoAdapter;
import me.app.coinwallet.ui.fragments.MnemonicRestoreFragment;
import me.app.coinwallet.ui.fragments.SelectWalletFragment;
import me.app.coinwallet.viewmodels.InitPageViewModel;
import me.app.coinwallet.data.wallets.WalletInfoEntry;

public class InitActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        loadFragment(SelectWalletFragment.newInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
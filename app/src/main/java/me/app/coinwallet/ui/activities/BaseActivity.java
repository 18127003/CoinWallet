package me.app.coinwallet.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseActivity extends AppCompatActivity {
    private WalletApplication application;

    protected static final Logger log = LoggerFactory.getLogger(BaseActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        application = (WalletApplication) getApplication();
        super.onCreate(savedInstanceState);
    }

    public WalletApplication getWalletApplication() {
        return application;
    }

    /***
     * load fragment with default frame id = R.id.frame_container
     */
    public void loadFragment(Fragment fragment){
        loadFragment(fragment, R.id.frame_container);
    }

    protected void loadFragment(Fragment fragment, int frameId){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

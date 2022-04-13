package me.app.coinwallet.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

}

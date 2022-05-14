package me.app.coinwallet.ui.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.ui.fragments.HomeFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public abstract class BaseActivity extends AppCompatActivity {
    public Configuration configuration;

    protected static final Logger log = LoggerFactory.getLogger(BaseActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        configuration = ((WalletApplication) getApplication()).getConfiguration();
        super.onCreate(savedInstanceState);

    }

    /***
     * load fragment with default frame id = R.id.frame_container
     */
    public void loadFragment(Class<? extends Fragment> fragment){
        loadFragment(fragment, R.id.frame_container);
    }

    protected void loadFragment(Class<? extends Fragment> fragment, int frameId, Object... args){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction =manager.beginTransaction();
        boolean fragmentPopped = manager.popBackStackImmediate (fragment.getSimpleName(), 0);
        Fragment current = manager.findFragmentByTag(fragment.getSimpleName());
        if(!fragmentPopped && current == null){
            try {
                current = (Fragment) fragment.getMethod("newInstance").invoke(null, args);
            } catch (NoSuchMethodException| IllegalAccessException| IllegalArgumentException| InvocationTargetException e){
                // Ignore
            }
            transaction.addToBackStack(null);
        }
        transaction.replace(frameId, current, fragment.getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

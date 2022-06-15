package me.app.coinwallet.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;

public class SingleFragmentActivity extends BaseActivity {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        Intent intent = getIntent();
        String appBarTitle = intent.getStringExtra(Constants.APP_BAR_TITLE_EXTRA_NAME);
        toolbar = findViewById(R.id.top_app_bar);
        toolbar.setTitle(appBarTitle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v-> finish());
        Class<? extends Fragment> fragment = (Class<? extends Fragment>) intent.getSerializableExtra(Constants.INIT_FRAGMENT_EXTRA_NAME);
        if(fragment!=null){
            loadFragment(fragment);
        }
    }
}
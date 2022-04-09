package me.app.coinwallet.activities;

import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import me.app.coinwallet.workers.BitcoinDownloadWorker;
import me.app.coinwallet.R;
import me.app.coinwallet.viewmodels.SetupPageViewModel;

public class MainActivity extends AppCompatActivity {
    private TextView sync;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sync = findViewById(R.id.sync);
        status = findViewById(R.id.status);
        final SetupPageViewModel walletViewModel = new ViewModelProvider(this)
                .get(SetupPageViewModel.class);
        walletViewModel.getSyncProgress().observe(this, s -> sync.setText(s));
        walletViewModel.getStatus().observe(this, (i)->{
            status.setText(i);
            if(i.equals(R.string.app_sync_completed)){
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        });
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(BitcoinDownloadWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
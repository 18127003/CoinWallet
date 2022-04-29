package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.LocalWalletListener;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletNotificationType;
import me.app.coinwallet.workers.BitcoinDownloadWorker;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class SetupPageViewModel extends AndroidViewModel implements LocalWalletListener {
    LocalWallet localWallet = LocalWallet.getInstance();
    private static final NetworkParameters PARAMETERS = TestNet3Params.get();
    private final MutableLiveData<String> syncProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> status = new MutableLiveData<>();

    public SetupPageViewModel(@NonNull Application application) {
        super(application);
        localWallet.subscribe(this);
    }

    public LiveData<String> getSyncProgress(){ return syncProgress; }

    public LiveData<Integer> getStatus(){
        return status;
    }

    public void initWallet(String label, String mnemonic){
        localWallet.setDirectory(getApplication().getFilesDir());
        localWallet.setParameters(PARAMETERS);
        localWallet.configWallet(label);
        if (mnemonic!=null){
            localWallet.restoreWallet(mnemonic);
        }
    }

    public void startSync(){
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(BitcoinDownloadWorker.class).build();
        WorkManager.getInstance(getApplication().getApplicationContext()).enqueue(workRequest);
    }

    @Override
    public void update(WalletNotificationType type, String content) {
        switch (type){
            case SYNC_PROGRESS:
                syncProgress.postValue(content);
                break;
            case SYNC_COMPLETED:
                status.postValue(R.string.app_sync_completed);
                break;
            case SETUP_COMPLETED:
                status.postValue(R.string.app_setup_completed);
                break;
        }
    }
}

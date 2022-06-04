package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletNotificationType;
import me.app.coinwallet.data.wallets.WalletInfoDao;
import me.app.coinwallet.data.wallets.WalletInfoDatabase;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.CryptoEngine;
import me.app.coinwallet.workers.BitcoinDownloadWorker;
import me.app.coinwallet.workers.BlockchainSyncService;
import org.bitcoinj.core.NetworkParameters;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitPageViewModel extends AndroidViewModel implements LocalWallet.EventListener {
    private final WalletInfoDao walletInfoDao;
    private final Application application;
    private final MutableLiveData<List<String>> mnemonicLabels = new MutableLiveData<>();
    LocalWallet localWallet = LocalWallet.getInstance();
    private final MutableLiveData<String> syncProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> status = new MutableLiveData<>();
    private final WorkManager workManager;
    private final MutableLiveData<String> selectedWalletLabel = new MutableLiveData<>();
    private final MutableLiveData<String> selectedMnemonic = new MutableLiveData<>();

    public InitPageViewModel(@NonNull final Application application) {
        super(application);
        this.application = application;
        localWallet.subscribe(this);
        workManager =  WorkManager.getInstance(application.getApplicationContext());
        walletInfoDao = WalletInfoDatabase.getDatabase(application.getApplicationContext()).walletInfoDao();
        refreshMnemonic();
    }

    public void setSelectedMnemonic(String selectedMnemonic) {
        this.selectedMnemonic.setValue(selectedMnemonic);
    }

    public void setSelectedWalletLabel(String selectedWalletLabel) {
        this.selectedWalletLabel.setValue(selectedWalletLabel);
    }

    public MutableLiveData<List<String>> getMnemonicLabels() {
        return mnemonicLabels;
    }

    public LiveData<List<WalletInfoEntry>> getWalletInfos() {
        return walletInfoDao.getAll();
    }


    public void saveWalletInfo(String label){
        walletInfoDao.insertOrUpdate(new WalletInfoEntry(label,"BITCOIN"));
    }

    public String decryptMnemonic(String label){
        SharedPreferences preferences = application.getSharedPreferences(
                application.getString(R.string.mnemonic_preference_file), Context.MODE_PRIVATE);
        String encrypted = preferences.getString(label, null);
        CryptoEngine engine = CryptoEngine.getInstance();
        return engine.decipher(label, encrypted);
    }

    public void refreshMnemonic(){
        SharedPreferences preferences = application.getSharedPreferences(application.getString(R.string.mnemonic_preference_file),
                Context.MODE_PRIVATE);
        mnemonicLabels.postValue(new ArrayList<>(preferences.getAll().keySet()));
    }

    public LiveData<String> getSyncProgress(){ return syncProgress; }

    public LiveData<Integer> getStatus(){
        return status;
    }

    public void initWallet(File directory, NetworkParameters parameters, InputStream checkpoints){
        localWallet.setDirectory(directory);
        localWallet.setParameters(parameters);
        localWallet.configWallet(selectedWalletLabel.getValue(), checkpoints);
        if (selectedMnemonic.getValue()!=null){
            localWallet.restoreWallet(selectedMnemonic.getValue());
        }
    }

    public void startSync(){
//        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BitcoinDownloadWorker.class).build();
//        workManager.enqueueUniqueWork("blockchain_sync", ExistingWorkPolicy.REPLACE, workRequest);
        BlockchainSyncService.startBackground(application);
    }

    public void cancelSync(){
        localWallet.stopWallet();
    }

    public boolean isEncrypted(){
        return localWallet.isEncrypted();
    }

    @Override
    public void update(WalletNotificationType type, LocalWallet.EventMessage<?> content) {
        switch (type){
            case SYNC_PROGRESS:
                String progress = String.valueOf((double) content.getContent());
                syncProgress.postValue(progress);
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

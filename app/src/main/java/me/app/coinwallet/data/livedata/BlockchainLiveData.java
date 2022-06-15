package me.app.coinwallet.data.livedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BlockchainLiveData {
    private final MutableLiveData<Boolean> blockchainSynced = new MutableLiveData<>();
    private final MutableLiveData<String> syncProgress = new MutableLiveData<>();

    public void updateProgress(double progress){
        syncProgress.postValue(String.valueOf(progress));
    }

    public void setSyncStatus(boolean status){
        blockchainSynced.postValue(status);
    }

    public LiveData<Boolean> getBlockchainSynced() {
        return blockchainSynced;
    }

    public LiveData<String> getSyncProgress() {
        return syncProgress;
    }
}

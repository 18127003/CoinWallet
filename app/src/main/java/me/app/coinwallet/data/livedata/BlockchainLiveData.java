package me.app.coinwallet.data.livedata;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.bitcoinj.WalletNotificationType;

public class BlockchainLiveData implements LocalWallet.EventListener {
    private final MutableLiveData<String> syncProgress = new MutableLiveData<>();
    private final MutableLiveData<BlockchainStatus> status = new MutableLiveData<>(BlockchainStatus.NOT_SYNCED);

    public MutableLiveData<BlockchainStatus> getStatus() {
        return status;
    }

    public LiveData<String> getSyncProgress() {
        return syncProgress;
    }

    private static BlockchainLiveData _instance;

    private BlockchainLiveData(){
        LocalWallet localWallet = LocalWallet.getInstance();
        localWallet.subscribe(this);
    }

    public static BlockchainLiveData get(){
        if(_instance == null){
            _instance = new BlockchainLiveData();
        }
        return _instance;
    }

    public void clear(){
        status.postValue(BlockchainStatus.NOT_SYNCED);
        syncProgress.postValue(null);
    }

    @Override
    public void update(WalletNotificationType type, @Nullable LocalWallet.EventMessage<?> content) {
        switch (type){
            case SYNC_STARTED:
                status.postValue(BlockchainStatus.SYNCING);
                break;
            case SYNC_PROGRESS:
                double progress = (double) content.getContent();
                syncProgress.postValue(String.valueOf(progress));
                break;
            case SYNC_COMPLETED:
                status.postValue(BlockchainStatus.SYNCED);
                break;
            case SYNC_STOPPED:
                status.postValue(BlockchainStatus.NOT_SYNCED);
                break;
            case SETUP_COMPLETED:
                status.postValue(BlockchainStatus.SYNC_START);
        }
    }

    public enum BlockchainStatus{
        SYNCING,
        SYNCED,
        NOT_SYNCED,
        SYNC_START
    }
}

package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.R;
import me.app.coinwallet.bitcoinj.WalletNotificationType;
import me.app.coinwallet.blockchain.BlockchainSyncService;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.data.wallets.WalletInfoDao;
import me.app.coinwallet.data.wallets.WalletInfoDatabase;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.utils.Utils;
import me.app.coinwallet.blockchain.StartBlockchainSyncService;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InitPageViewModel extends AndroidViewModel {
    private final WalletInfoDao walletInfoDao;
    private final WalletApplication application;

    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final BlockchainLiveData blockchainLiveData = BlockchainLiveData.get();
    private final MutableLiveData<WalletInfoEntry> selectedWallet = new MutableLiveData<>();
    private final List<Integer> accounts;

    public boolean createNewAccount = false;

    public InitPageViewModel(@NonNull final Application application) {
        super(application);
        this.application = (WalletApplication) application;
        walletInfoDao = WalletInfoDatabase.getDatabase(application.getApplicationContext()).walletInfoDao();
        accounts = localWallet.allAccounts();
    }

    public void setSelectedWallet(WalletInfoEntry entry) {
        this.selectedWallet.postValue(entry);
    }

    public LiveData<WalletInfoEntry> getSelectedWallet(){ return selectedWallet; }

    public LiveData<List<WalletInfoEntry>> getWalletInfos() {
        return walletInfoDao.getByAccounts(accounts);
    }

    public WalletInfoEntry saveWalletInfo(WalletInfoEntry info){
        int accountIndex = accounts.size();
        info.setAccountIndex(accountIndex);
        createNewAccount = true;
        walletInfoDao.insertOrUpdate(info);
        return info;
    }

    public LiveData<BlockchainLiveData.BlockchainStatus> getStatus(){
        return blockchainLiveData.getStatus();
    }

    public void initWallet(WalletInfoEntry walletInfoEntry, InputStream checkpoints){
        LocalWallet.WalletInfo walletInfo = new LocalWallet.WalletInfo();
        walletInfo.checkPoint = checkpoints;
        walletInfo.label = walletInfoEntry.getLabel();
        walletInfo.accountIndex = walletInfoEntry.getAccountIndex();
        walletInfo.parameters = NetworkParameters.fromID(walletInfoEntry.getNetworkId());
        localWallet.registerAccount(walletInfo);
    }

    public void startSync(){
        StartBlockchainSyncService.schedule(application, BlockchainSyncService.ACTION_START_SYNC);
        // Start if offline
        if (!Utils.hasInternetAccess(application)){
            localWallet.initWalletOffline();
        }
    }

    public void addAccount(int accountIndex, String password){
        localWallet.addAccount(accountIndex, password);
    }

    public boolean isEncrypted(){
        return localWallet.isEncrypted();
    }
}

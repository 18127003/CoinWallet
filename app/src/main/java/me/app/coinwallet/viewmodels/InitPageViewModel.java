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
import me.app.coinwallet.data.wallets.WalletInfoDao;
import me.app.coinwallet.data.wallets.WalletInfoDatabase;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.utils.Utils;
import me.app.coinwallet.blockchain.StartBlockchainSyncService;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InitPageViewModel extends AndroidViewModel implements LocalWallet.EventListener {
    private final WalletInfoDao walletInfoDao;
    private final WalletApplication application;
    private final MutableLiveData<List<String>> mnemonicLabels = new MutableLiveData<>();
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final MutableLiveData<String> syncProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> status = new MutableLiveData<>();
    private final MutableLiveData<WalletInfoEntry> selectedWallet = new MutableLiveData<>();
    private List<Integer> accounts;
    private DeterministicSeed restoreSeed;
    public boolean createNewAccount = false;

    public InitPageViewModel(@NonNull final Application application) {
        super(application);
        this.application = (WalletApplication) application;
        localWallet.subscribe(this);
        walletInfoDao = WalletInfoDatabase.getDatabase(application.getApplicationContext()).walletInfoDao();
        refreshMnemonic();
        accounts = localWallet.allAccounts();
    }

    public void setSelectedWallet(WalletInfoEntry entry) {
        this.selectedWallet.postValue(entry);
    }

    public LiveData<WalletInfoEntry> getSelectedWallet(){ return selectedWallet; }

    public MutableLiveData<List<String>> getMnemonicLabels() {
        return mnemonicLabels;
    }

    public LiveData<List<WalletInfoEntry>> getWalletInfos() {
        return walletInfoDao.getByAccounts(accounts);
    }


    public WalletInfoEntry saveWalletInfo(String label){
        int accountIndex = accounts.size();
        createNewAccount = true;
        WalletInfoEntry entry = new WalletInfoEntry(accountIndex, label,"BITCOIN");
        walletInfoDao.insertOrUpdate(entry);
        return entry;
    }

    public String decryptMnemonic(String label){
        SharedPreferences preferences = application.getSharedPreferences(
                application.getString(R.string.mnemonic_preference_file), Context.MODE_PRIVATE);
        String encrypted = preferences.getString(label, null);
        restoreSeed = WalletUtil.decryptMnemonic(encrypted, label);
        if(restoreSeed != null){
          return restoreSeed.getMnemonicString();
        }
        return null;
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

    public void restoreWallet(String mnemonic) throws UnreadableWalletException {
        if(restoreSeed == null){
            restoreSeed = new DeterministicSeed(mnemonic, null, "", 0L);
        }
    }

    public void initWallet(InputStream checkpoints){
        LocalWallet.WalletInfo walletInfo = new LocalWallet.WalletInfo();
        walletInfo.checkPoint = checkpoints;
        WalletInfoEntry entry = selectedWallet.getValue();
        walletInfo.label = entry.getLabel();
        walletInfo.accountIndex = entry.getAccountIndex();
        if (restoreSeed != null){
            walletInfo.restoreSeed = restoreSeed;
        }
        localWallet.configWalletAppKit(walletInfo);
    }

    public void startSync(){
        StartBlockchainSyncService.schedule(application);
        // Start if offline
        if (!Utils.hasInternetAccess(application)){
            localWallet.initWalletOffline();
        }
    }

    public void addAccount(String password){
        int account = selectedWallet.getValue().getAccountIndex();
        localWallet.addAccount(account, password);
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

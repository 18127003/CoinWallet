package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.R;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.blockchain.BlockchainSyncService;
import me.app.coinwallet.blockchain.StartBlockchainSyncService;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.data.wallets.WalletInfoDao;
import me.app.coinwallet.data.wallets.WalletInfoDatabase;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RestoreMnemonicViewModel extends AndroidViewModel {
    private final Application application;
    private DeterministicSeed restoreSeed;
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final WalletInfoDao walletInfoDao;
    private final MutableLiveData<List<String>> mnemonicLabels = new MutableLiveData<>();

    public RestoreMnemonicViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        refreshMnemonic();
        walletInfoDao = WalletInfoDatabase.getDatabase(application.getApplicationContext()).walletInfoDao();
    }

    public LiveData<BlockchainLiveData.BlockchainStatus> getStatus(){return BlockchainLiveData.get().getStatus();}

    public MutableLiveData<List<String>> getMnemonicLabels() {
        return mnemonicLabels;
    }

    public void startSync(){
        StartBlockchainSyncService.schedule(application, BlockchainSyncService.ACTION_RESTORE_MNEMONIC);
    }

    public void restoreWallet(NetworkParameters parameters, String mnemonic, List<HDPath> accounts,
                              InputStream checkpoints) throws UnreadableWalletException {
        if(restoreSeed == null){
            restoreSeed = new DeterministicSeed(mnemonic, null, "", 0L);
        }
        LocalWallet.WalletInfo walletInfo = new LocalWallet.WalletInfo();
        walletInfo.checkPoint = checkpoints;
        walletInfo.parameters = parameters;
        walletInfo.restoreAccounts = accounts;
        walletInfo.restoreSeed = restoreSeed;
        walletInfo.accountIndex = Integer.MAX_VALUE;
        localWallet.registerAccount(walletInfo);
    }

    public void saveWalletInfos(List<WalletInfoEntry> infos){
        walletInfoDao.insertOrUpdateMultiple(infos);
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
}

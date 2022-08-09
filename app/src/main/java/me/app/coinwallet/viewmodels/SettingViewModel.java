package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.R;
import me.app.coinwallet.blockchain.BlockchainSyncService;
import me.app.coinwallet.data.configuration.ConfigurationOption;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.utils.LocaleUtil;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingViewModel extends AndroidViewModel {
    private final LocalWallet localWallet = LocalWallet.getInstance();

    private final MutableLiveData<String> mnemonic = new MutableLiveData<>();
    private final WalletLiveData walletLiveData = WalletLiveData.get();
    private DeterministicSeed seed;

    public SettingViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getAddress(){return walletLiveData.getCurrentReceivingAddress();}

    public String getAccountPath(){
        HDPath accountPath = localWallet.getCurrentAccountPath();
        if(accountPath == null){
            return "";
        }
        return accountPath.toString();
    }

    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword){
        if(newPassword.equals(confirmPassword)){
            try {
                localWallet.changePassword(currentPassword, newPassword);
                return true;
            } catch (Wallet.BadWalletEncryptionKeyException e){
                return false;
            }
        }
        return false;
    }

    public void decryptMnemonic(String password){
        seed = localWallet.seed(password);
        mnemonic.postValue(seed.getMnemonicString());
    }

    public LiveData<String> getMnemonic() {
        return mnemonic;
    }

    public boolean encryptMnemonic(){
        String label = localWallet.getLabel();
        String prefName = getApplication().getString(R.string.mnemonic_preference_file);
        SharedPreferences preferences = getApplication().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String encrypted = WalletUtil.encryptMnemonic(seed, label);
        if (encrypted == null){
            return false;
        }
        editor.putString(label, encrypted);
        editor.apply();
        return true;
    }

    public void logout(){
        WalletLiveData.get().clear();
        BlockchainLiveData.get().clear();
        BlockchainSyncService.SHOULD_RESTART.set(false);
        BlockchainSyncService.stop();
    }
}

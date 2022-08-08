package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.R;
import me.app.coinwallet.blockchain.BlockchainSyncService;
import me.app.coinwallet.data.language.LanguageOption;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.utils.LocaleUtil;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

public class SettingViewModel extends AndroidViewModel {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final List<LanguageOption> languages;
    private final MutableLiveData<String> mnemonic = new MutableLiveData<>();
    private final WalletLiveData walletLiveData = WalletLiveData.get();
    private DeterministicSeed seed;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        languages = new ArrayList<>();
        languages.add(new LanguageOption(1, "English", "en"));
        languages.add(new LanguageOption(2, "Vietnam", "vi"));
    }

    public LiveData<String> getAddress(){return walletLiveData.getCurrentReceivingAddress();}

    public String getAccountPath(){
        HDPath accountPath = localWallet.getCurrentAccountPath();
        if(accountPath == null){
            return "";
        }
        return accountPath.toString();
    }

    public List<LanguageOption> getLanguages() {
        return languages;
    }

    public void changeLanguage(String languageCode, OnConfigurationChange callback){
        LocaleUtil.setLocale(getApplication().getApplicationContext(), languageCode);
        callback.onLocaleChange();
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

    public String getSelectedLanguage(Context context){
        return LocaleUtil.getLanguage(context);
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

    public interface OnConfigurationChange{
        void onLocaleChange();
    }

    public void logout(){
        WalletLiveData.get().clear();
        BlockchainLiveData.get().clear();
        BlockchainSyncService.SHOULD_RESTART.set(false);
        BlockchainSyncService.stop();
    }
}

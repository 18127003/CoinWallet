package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.R;
import me.app.coinwallet.data.wallets.WalletInfoDao;
import me.app.coinwallet.data.wallets.WalletInfoDatabase;
import me.app.coinwallet.data.wallets.WalletInfoEntry;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.CryptoEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitPageViewModel extends AndroidViewModel {
    private final WalletInfoDao walletInfoDao;
    private final Application application;
    private MutableLiveData<List<String>> mnemonicLabels = new MutableLiveData<>();

    public InitPageViewModel(@NonNull final Application application) {
        super(application);
        this.application = application;
        walletInfoDao = WalletInfoDatabase.getDatabase(application.getApplicationContext()).walletInfoDao();
        refreshMnemonic();
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
}

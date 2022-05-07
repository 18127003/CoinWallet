package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletNotificationType;
import me.app.coinwallet.exceptions.MnemonicInaccessibleException;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.CryptoEngine;
import org.bitcoinj.core.Transaction;
import java.util.List;

public class HomePageViewModel extends AndroidViewModel implements LocalWallet.EventListener {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final MutableLiveData<String> balance = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> history = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> encryptBtnLabel = new MutableLiveData<>();
    private final Application application;
    private final BiometricUtil biometricUtil;

    public LiveData<String> getBalance(){ return balance; }

    public LiveData<List<Transaction>> getHistory(){return history;}

    public LiveData<String> getAddress(){return address;}

    public void extractMnemonic() throws MnemonicInaccessibleException{
        String mnemonicCode = localWallet.wallet().getKeyChainSeed().getMnemonicString();
        if (mnemonicCode == null){
            throw new MnemonicInaccessibleException();
        }
        biometricUtil.setAuthenticationCallback(new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                encryptMnemonic(mnemonicCode);
            }
        });
        biometricUtil.authenticate();
    }

    private void encryptMnemonic(String mnemonicCode){

        CryptoEngine cryptoEngine = CryptoEngine.getInstance();
        String walletLabel = localWallet.getLabel();
        String encrypted = cryptoEngine.cipher(walletLabel, mnemonicCode);
        Log.e("HD","Encrypted to "+encrypted);
        SharedPreferences preferences = application.getSharedPreferences(
                application.getString(R.string.mnemonic_preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(walletLabel, encrypted);
        editor.apply();
    }

    public void checkUtxo(){
        localWallet.check();
    }

    public MutableLiveData<String> getEncryptBtnLabel() {
        return encryptBtnLabel;
    }

    public void encryptOrDecrypt(String password){
        if(localWallet.isEncrypted()){
            localWallet.decryptWallet(password);
        } else {
            localWallet.encryptWallet(password);
        }
        refreshEncryptBtn();
    }

    public void encryptCheck(){
        Log.e("HD","Is encrypt: "+localWallet.wallet().isEncrypted());
    }

    public void refresh(){
        balance.postValue(localWallet.getPlainBalance());
        history.postValue(localWallet.history());
        address.postValue(localWallet.getAddress().toString());
    }

    private void refreshEncryptBtn(){
        encryptBtnLabel.postValue(localWallet.isEncrypted()?"Decrypt":"Encrypt");
    }

    public HomePageViewModel(Application application, BiometricUtil biometricUtil){
        super(application);
        this.application = application;
        this.biometricUtil = biometricUtil;
        localWallet.subscribe(this);
        refreshEncryptBtn();
        refresh();
    }

    @Override
    public void update(WalletNotificationType type, String content) {
        switch (type){
            case TX_ACCEPTED:
                refresh();
                break;
            case TX_RECEIVED:
                refresh();
                break;
            case BALANCE_CHANGED:
                refresh();
                break;
        }
    }
}

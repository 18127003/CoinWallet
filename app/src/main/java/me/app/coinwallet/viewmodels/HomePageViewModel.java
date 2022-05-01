package me.app.coinwallet.viewmodels;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.WalletNotificationType;
import org.bitcoinj.core.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HomePageViewModel extends ViewModel implements LocalWallet.EventListener {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final MutableLiveData<String> balance = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> history = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> encryptBtnLabel = new MutableLiveData<>();

    public LiveData<String> getBalance(){ return balance; }

    public LiveData<List<Transaction>> getHistory(){return history;}

    public LiveData<String> getAddress(){return address;}

    public void extractMnemonic(File root){
        String mnemonicCode = localWallet.wallet().getKeyChainSeed().getMnemonicString();
        Log.e("HD","Mnemonic code: "+mnemonicCode);
        File file = new File(root,"mnemonic");
        try {
            FileOutputStream fis = new FileOutputStream(file);
            fis.write(mnemonicCode.getBytes());
            fis.close();
        } catch (FileNotFoundException ex){
            Log.e("HD","File not found "+ file.getName());
        } catch (IOException e){
            Log.e("HD","File write fail "+file.getName());
        }
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

    public HomePageViewModel(){
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

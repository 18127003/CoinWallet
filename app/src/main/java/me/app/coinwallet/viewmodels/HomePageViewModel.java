package me.app.coinwallet.viewmodels;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.LocalWalletListener;
import me.app.coinwallet.WalletNotificationType;
import org.bitcoinj.core.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class HomePageViewModel extends ViewModel implements LocalWalletListener {
    private LocalWallet localWallet = LocalWallet.getInstance();
    private final MutableLiveData<String> balance = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> history = new MutableLiveData<>();

    public LiveData<String> getBalance(){ return balance; }

    public LiveData<List<Transaction>> getHistory(){return history;}

    public String getAddress(){return localWallet.getAddress().toString();}

    public void send(String sendAddress, String value){
        try{
            double doubleValue = Double.parseDouble(value);
            localWallet.send(sendAddress, doubleValue);
        } catch (NumberFormatException e){
            Log.e("HD","Send amount not in number format");
        }
    }

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

    public void checkUxto(){
        localWallet.check();
    }

    public void refresh(){
        balance.postValue(localWallet.getPlainBalance());
        history.postValue(localWallet.history());
    }

    public HomePageViewModel(){

        localWallet.subscribe(this);
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

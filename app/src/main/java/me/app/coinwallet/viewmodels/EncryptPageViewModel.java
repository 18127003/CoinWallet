package me.app.coinwallet.viewmodels;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.LocalWalletListener;
import me.app.coinwallet.WalletNotificationType;
import org.bitcoinj.crypto.MnemonicCode;

import java.io.*;
import java.util.Arrays;

public class EncryptPageViewModel extends ViewModel implements LocalWalletListener {
    private LocalWallet localWallet = LocalWallet.getInstance();

    public void encrypt(CharSequence password){
        localWallet.wallet().encrypt(password);
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

    @Override
    public void update(WalletNotificationType type, String content) {

    }
}

package me.app.coinwallet.viewmodels;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.WalletNotificationType;

import java.io.*;

public class EncryptPageViewModel extends ViewModel implements LocalWallet.EventListener {
    private LocalWallet localWallet = LocalWallet.getInstance();

    public void encrypt(CharSequence password){
        localWallet.wallet().encrypt(password);
    }


    @Override
    public void update(WalletNotificationType type, String content) {

    }
}

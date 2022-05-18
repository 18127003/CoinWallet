package me.app.coinwallet.viewmodels;

import androidx.lifecycle.ViewModel;
import me.app.coinwallet.LocalWallet;

public class AuthenticateViewModel extends ViewModel {
    LocalWallet localWallet = LocalWallet.getInstance();

    public boolean checkPassword(String password){
        try{
            return localWallet.checkPassword(password);
        } catch (IllegalStateException e){
            localWallet.encryptWallet(password);
            return true;
        }
    }
}

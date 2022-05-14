package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.data.addressbook.AddressBookDao;
import me.app.coinwallet.data.addressbook.AddressBookDatabase;
import me.app.coinwallet.data.addressbook.AddressBookEntry;
import me.app.coinwallet.data.livedata.WalletLiveData;

import java.util.List;

public class TransferPageViewModel extends AndroidViewModel {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final WalletLiveData walletLiveData;
    private final MutableLiveData<String> sendToAddress = new MutableLiveData<>();
    private final AddressBookDao addressBookDao;

    public TransferPageViewModel(@NonNull Application application) {
        super(application);
        walletLiveData = new WalletLiveData(localWallet);
        addressBookDao = AddressBookDatabase.getDatabase(application.getApplicationContext()).addressBookDao();
        walletLiveData.refreshAvailableBalance();
    }

    public void setSendToAddress(String address){
        sendToAddress.setValue(address);
    }

    public LiveData<String> getSendToAddress() {
        return sendToAddress;
    }

    public void saveToAddressBook(String label, String address){
        addressBookDao.insertOrUpdate(new AddressBookEntry(address, label));
    }

    public void send(String sendAddress, String value, String password){
        try{
            double doubleValue = Double.parseDouble(value);
            localWallet.send(sendAddress, doubleValue, password);
        } catch (NumberFormatException e){
            Log.e("HD","Send amount not in number format "+value);
        }
    }

    public LiveData<List<AddressBookEntry>> getAddressBook() {
        return addressBookDao.getAll();
    }

    public boolean isWalletEncrypted(){
        return localWallet.isEncrypted();
    }
}

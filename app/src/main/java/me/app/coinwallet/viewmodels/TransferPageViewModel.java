package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.data.addressbook.AddressBookDao;
import me.app.coinwallet.data.addressbook.AddressBookDatabase;
import me.app.coinwallet.data.addressbook.AddressBookEntry;

import java.util.List;

public class TransferPageViewModel extends AndroidViewModel {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final MutableLiveData<String> balance = new MutableLiveData<>();
//    private final MutableLiveData<List<AddressBookEntry>> addressBook = new MutableLiveData<>();
    private final AddressBookDao addressBookDao;

    public TransferPageViewModel(Application application){
        super(application);
        addressBookDao = AddressBookDatabase.getDatabase(application.getApplicationContext()).addressBookDao();
        balance.postValue(localWallet.getPlainBalance());
    }

    public LiveData<List<AddressBookEntry>> getAddressBook() {
        return addressBookDao.getAll();
    }

    public MutableLiveData<String> getBalance() {
        return balance;
    }

    public void send(String sendAddress, String value, String password){
        try{
            double doubleValue = Double.parseDouble(value);
            localWallet.send(sendAddress, doubleValue, password);
        } catch (NumberFormatException e){
            Log.e("HD","Send amount not in number format");
        }
    }

    public void saveToAddressBook(String label, String address){
        addressBookDao.insertOrUpdate(new AddressBookEntry(address, label));
    }

    public boolean isWalletEncrypted(){
        return localWallet.isEncrypted();
    }
}

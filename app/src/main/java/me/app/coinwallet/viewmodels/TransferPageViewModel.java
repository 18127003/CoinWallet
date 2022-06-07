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
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

import java.util.List;

public class TransferPageViewModel extends AndroidViewModel {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final AddressBookDao addressBookDao;
    private BitcoinURI bitcoinURI;

    public TransferPageViewModel(@NonNull Application application) {
        super(application);
        addressBookDao = AddressBookDatabase.getDatabase(application.getApplicationContext()).addressBookDao();
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

    public void extractUri(String uri) throws BitcoinURIParseException {
        bitcoinURI = new BitcoinURI(uri);
    }

    public double getAmountFromUri(){
        return bitcoinURI.getAmount().toBtc().doubleValue();
    }

    public String getSendToFromUri(){
        return bitcoinURI.getAddress().toString();
    }

    public LiveData<List<AddressBookEntry>> getAddressBook() {
        return addressBookDao.getAll();
    }
}

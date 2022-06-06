package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.WalletNotificationType;
import me.app.coinwallet.data.addressbook.AddressBookDao;
import me.app.coinwallet.data.addressbook.AddressBookDatabase;
import me.app.coinwallet.data.addressbook.AddressBookEntry;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.data.marketcap.*;
import me.app.coinwallet.data.transaction.MonthlyReport;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.exceptions.MnemonicInaccessibleException;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.CryptoEngine;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.Transaction;
import java.util.List;
import java.util.Locale;

public class HomePageViewModel extends AndroidViewModel implements LocalWallet.EventListener {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final WalletApplication application;
    private final WalletLiveData walletLiveData;
    final MarketChartDao chartDao;
    private final MediatorLiveData<List<ChartEntry>> chartLiveData = new MediatorLiveData<>();
    private LiveData<List<ChartEntry>> underlyingChartLiveData;

    public LiveData<String> getBalance(){ return walletLiveData.getAvailableBalance(); }

    public MutableLiveData<List<MonthlyReport>> getMonthlyReports() {
        return walletLiveData.getMonthlyReports();
    }

    public LiveData<String> getAddress(){return walletLiveData.getCurrentReceivingAddress();}

    public MediatorLiveData<List<ChartEntry>> getChartLiveData() {
        return chartLiveData;
    }

    public void fetchChart() {
        if (underlyingChartLiveData != null)
            chartLiveData.removeSource(underlyingChartLiveData);
        underlyingChartLiveData = chartDao.findAll();
        chartLiveData.addSource(underlyingChartLiveData, chartLiveData::setValue);
    }

    public String extractMnemonic() throws MnemonicInaccessibleException{
        String mnemonicCode = localWallet.wallet().getKeyChainSeed().getMnemonicString();
        if (mnemonicCode == null){
            throw new MnemonicInaccessibleException();
        }
        return mnemonicCode;
    }

    public void encryptMnemonic(String mnemonicCode){
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

    public HomePageViewModel(Application application){
        super(application);
        this.application = (WalletApplication) application;
        localWallet.subscribe(this);
        chartDao = MarketChartRepository.get(this.application).marketChartDao();
//        MarketCapRepository.get(this.application).maybeRequestTrend();
        walletLiveData = new WalletLiveData(localWallet);
        walletLiveData.refreshAll();
    }

    @Override
    public void update(WalletNotificationType type, LocalWallet.EventMessage<?> content) {
        switch (type){
            case TX_ACCEPTED:
                walletLiveData.refreshAvailableBalance();
                walletLiveData.refreshTxHistory((Transaction) content.getContent());
                walletLiveData.refreshAvailableBalance();
                break;
            case TX_RECEIVED:
                walletLiveData.refreshCurrentReceivingAddress();
                walletLiveData.refreshExpectedBalance();
                walletLiveData.refreshTxHistory((Transaction) content.getContent());
                break;
        }
    }
}

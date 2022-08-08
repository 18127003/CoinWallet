package me.app.coinwallet.data.livedata;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.bitcoinj.WalletNotificationType;
import me.app.coinwallet.data.transaction.MonthlyReport;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.utils.Utils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WalletLiveData implements LocalWallet.EventListener {
    private final MutableLiveData<String> availableBalance = new MutableLiveData<>();
    private final MutableLiveData<String> expectedBalance = new MutableLiveData<>();
    private final MutableLiveData<String> currentReceivingAddress = new MutableLiveData<>();
    private final MutableLiveData<List<MonthlyReport>> monthlyReports = new MutableLiveData<>();
    private final LocalWallet wallet;
    private Multimap<String, TransactionWrapper> monthlyReportMap;
    private final MutableLiveData<TransactionWrapper> lastTx = new MutableLiveData<>();
    private TransactionConfidence.ConfidenceType filter;

    private static WalletLiveData _instance;

    private WalletLiveData(){
        wallet = LocalWallet.getInstance();
        wallet.subscribe(this);
    }

    public static WalletLiveData get(){
        if(_instance == null){
            _instance = new WalletLiveData();
        }
        return _instance;
    }

    public void filterTx(TransactionConfidence.ConfidenceType type){
        filter = type;
        refreshTxHistory(type);
    }

    public void refreshAvailableBalance(){ availableBalance.postValue(wallet.getPlainBalance()); wallet.check();}

    public void refreshExpectedBalance(){ expectedBalance.postValue(wallet.getExpectedBalance()); }

    public void refreshCurrentReceivingAddress(){
        Address address = wallet.getAddress();
        if(address!=null){
            currentReceivingAddress.postValue(wallet.getAddress().toString());
        }
    }

    public void refreshLatestTx(TransactionWrapper tx){
        lastTx.postValue(tx);
    }

    public void refreshTxHistory(TransactionConfidence.ConfidenceType type){
        List<Transaction> txs = wallet.history();
        if(type!=null){
            txs = txs.stream().filter(tx->tx.getConfidence().getConfidenceType().equals(type))
                    .collect(Collectors.toList());
        }

        List<TransactionWrapper> txWrappers = wrapTransaction(txs);
        monthlyReportMap = splitTransactions(txWrappers);
        monthlyReports.postValue(getMonthlyReportList(monthlyReportMap));
        if(txs.size() > 0){
            refreshLatestTx(txWrappers.get(0));
        }
    }

    public void refreshTxHistory(Transaction transaction, TransactionConfidence.ConfidenceType type){
        if (monthlyReportMap == null){
            refreshTxHistory(type);
        }
        TransactionWrapper txWrapper = TransactionWrapper.from(transaction, wallet.wallet());
        refreshLatestTx(txWrapper);
        if(type != null && !transaction.getConfidence().getConfidenceType().equals(type)){
            return;
        }

        String time = Utils.getMonthYearFromDate(txWrapper.getTime());

        if (monthlyReportMap.containsEntry(time, txWrapper)){
            monthlyReportMap.remove(time, txWrapper);
        }
        monthlyReportMap.put(time, txWrapper);
        monthlyReports.postValue(getMonthlyReportList(monthlyReportMap));
    }

    public LiveData<List<MonthlyReport>> getMonthlyReports() {
        return monthlyReports;
    }

    public LiveData<String> getExpectedBalance() {
        return expectedBalance;
    }

    public LiveData<String> getAvailableBalance() {
        return availableBalance;
    }

    public LiveData<String> getCurrentReceivingAddress() {
        return currentReceivingAddress;
    }

    private List<TransactionWrapper> wrapTransaction(List<Transaction> transactions){
        return transactions.stream().map(tx->TransactionWrapper.from(tx, wallet.wallet())).collect(Collectors.toList());
    }

    private Multimap<String, TransactionWrapper> splitTransactions(List<TransactionWrapper> transactionWrappers){
        Multimap<String, TransactionWrapper> monthlyReports = HashMultimap.create();
        for(TransactionWrapper tx: transactionWrappers){
            String time = Utils.getMonthYearFromDate(tx.getTime());
            monthlyReports.put(time, tx);
        }
        return monthlyReports;
    }

    private List<MonthlyReport> getMonthlyReportList(Multimap<String, TransactionWrapper> monthlyReports){
        List<MonthlyReport> result = new ArrayList<>(monthlyReports.keySet().size());
        monthlyReports.asMap().forEach((key, value) -> result.add(new MonthlyReport(key, value)));
        return result;
    }

    public LiveData<TransactionWrapper> getLatestTx(){
        return lastTx;
    }

    public void clear(){
        availableBalance.postValue(null);
        expectedBalance.postValue(null);
        currentReceivingAddress.postValue(null);
        monthlyReports.postValue(null);
        lastTx.postValue(null);
    }

    @Override
    public void update(WalletNotificationType type, @Nullable LocalWallet.EventMessage<?> content) {
        switch (type){
            case TX_RECEIVED:
                refreshCurrentReceivingAddress();
                refreshExpectedBalance();
                refreshTxHistory((Transaction) content.getContent(),filter);
                break;
            case TX_ACCEPTED:
                refreshAvailableBalance();
                refreshTxHistory((Transaction) content.getContent(),filter);
                break;
            case SETUP_COMPLETED:
                Log.e("HD","refresh wallet");
                refreshAvailableBalance();
                refreshTxHistory(filter);
                refreshExpectedBalance();
                refreshCurrentReceivingAddress();
                break;
            case ACCOUNT_ADDED:
                Log.e("HD","refresh address");
                refreshCurrentReceivingAddress();
                break;
        }
    }
}

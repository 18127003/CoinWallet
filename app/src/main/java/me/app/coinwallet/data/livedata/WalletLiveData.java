package me.app.coinwallet.data.livedata;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.WalletNotificationType;
import me.app.coinwallet.data.transaction.MonthlyReport;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.utils.Utils;
import org.bitcoinj.core.Transaction;
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
    private final MutableLiveData<Boolean> isActive = new MutableLiveData<>();

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

    public void refreshAvailableBalance(){ availableBalance.postValue(wallet.getPlainBalance()); wallet.check();}

    public void refreshExpectedBalance(){ expectedBalance.postValue(wallet.getExpectedBalance()); }

    public void refreshCurrentReceivingAddress(){ currentReceivingAddress.postValue(wallet.getAddress().toString()); }

    public void refreshIsActive(){ isActive.postValue(wallet.wallet()!=null); }

    public void refreshTxHistory(){
        List<TransactionWrapper> txs = wrapTransaction(wallet.history());
        monthlyReportMap = splitTransactions(txs);
        monthlyReports.postValue(getMonthlyReportList(monthlyReportMap));
    }

    public void refreshTxHistory(Transaction transaction){
        TransactionWrapper txWrapper = TransactionWrapper.from(transaction, wallet.wallet());
        String time = Utils.getMonthYearFromDate(txWrapper.getTime());
        if (monthlyReportMap == null){
            refreshTxHistory();
        }
        if (monthlyReportMap.containsEntry(time, txWrapper)){
            monthlyReportMap.remove(time, txWrapper);
        }
        monthlyReportMap.put(time, txWrapper);
        monthlyReports.postValue(getMonthlyReportList(monthlyReportMap));
    }

    public MutableLiveData<List<MonthlyReport>> getMonthlyReports() {
        return monthlyReports;
    }

    public MutableLiveData<Boolean> getIsActive() { return isActive; }

    public MutableLiveData<String> getExpectedBalance() {
        return expectedBalance;
    }

    public MutableLiveData<String> getAvailableBalance() {
        return availableBalance;
    }

    public MutableLiveData<String> getCurrentReceivingAddress() {
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

    public TransactionWrapper getLatestTx(){
        Transaction tx = wallet.getLatestTx();
        if(tx != null){
            return TransactionWrapper.from(tx, wallet.wallet());
        }
        return null;
    }

    @Override
    public void update(WalletNotificationType type, @Nullable LocalWallet.EventMessage<?> content) {
        switch (type){
            case TX_RECEIVED:
                refreshCurrentReceivingAddress();
                refreshExpectedBalance();
                refreshTxHistory((Transaction) content.getContent());
                break;
            case TX_ACCEPTED:
                refreshAvailableBalance();
                refreshTxHistory((Transaction) content.getContent());
                refreshAvailableBalance();
                break;
            case SETUP_COMPLETED:
                Log.e("HD","refresh wallet");
                refreshIsActive();
                refreshAvailableBalance();
                refreshTxHistory();
                refreshExpectedBalance();
                refreshCurrentReceivingAddress();
                break;
        }
    }
}

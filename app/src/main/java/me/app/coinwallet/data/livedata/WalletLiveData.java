package me.app.coinwallet.data.livedata;

import androidx.lifecycle.MutableLiveData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.data.transaction.MonthlyReport;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.utils.Utils;
import org.bitcoinj.core.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WalletLiveData {
    private final MutableLiveData<String> availableBalance = new MutableLiveData<>();
    private final MutableLiveData<String> expectedBalance = new MutableLiveData<>();
    private final MutableLiveData<String> currentReceivingAddress = new MutableLiveData<>();
    private final MutableLiveData<List<MonthlyReport>> monthlyReports = new MutableLiveData<>();
    private final LocalWallet wallet;
    private Multimap<String, TransactionWrapper> monthlyReportMap;

    public WalletLiveData(LocalWallet wallet){
        this.wallet = wallet;
    }

    public void refreshAvailableBalance(){ availableBalance.postValue(wallet.getPlainBalance()); }

    public void refreshExpectedBalance(){ expectedBalance.postValue(wallet.getExpectedBalance()); }

    public void refreshCurrentReceivingAddress(){ currentReceivingAddress.postValue(wallet.getAddress().toString()); }

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

    public void refreshAll(){
        refreshAvailableBalance();
        refreshExpectedBalance();
        refreshCurrentReceivingAddress();
        refreshTxHistory();
    }

    public MutableLiveData<List<MonthlyReport>> getMonthlyReports() {
        return monthlyReports;
    }

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
}

package me.app.coinwallet.data.transaction;

import org.bitcoinj.core.Coin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MonthlyReport {
    private final List<TransactionWrapper> transactions;
    private final String time;

    public MonthlyReport(String time, Collection<TransactionWrapper> transactions){
        this.transactions = new ArrayList<>(transactions);
        this.time = time;
    }

    /***
     * Get month's tx list. Default to be sorted from the most recent. May have sort options in the future.
     */
    public List<TransactionWrapper> getTransactions() {
        return transactions.stream().sorted(TransactionWrapper.SORT_BY_UPDATE_TIME).collect(Collectors.toList());
    }

    public String getTime() {
        return time;
    }

    public String sumIncome(){
        Coin result = transactions.stream()
                .filter((tx)-> !tx.isSend())
                .map(TransactionWrapper::getAmount).reduce(Coin::add).orElse(Coin.ZERO);
        return result.toFriendlyString();
    }

    public String sumOutcome(){
        Coin result = transactions.stream()
                .filter(TransactionWrapper::isSend)
                .map(TransactionWrapper::getAmount).reduce(Coin::add).orElse(Coin.ZERO);
        return result.toFriendlyString();
    }
}

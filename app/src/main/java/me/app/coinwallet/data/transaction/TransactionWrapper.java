package me.app.coinwallet.data.transaction;

import androidx.annotation.Nullable;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.Wallet;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class TransactionWrapper implements Serializable {
    private final Sha256Hash txId;
    private final Date time;
    private final Address receiver;
    private TransactionConfidence.ConfidenceType status;
    private Integer confirmNum;
    private final Coin fee;
    private final Coin amount;
    private final boolean isSend;

    public TransactionWrapper(Sha256Hash txId, Date time, @Nullable Address receiver, TransactionConfidence.ConfidenceType status,
                              Integer confirmNum, Coin fee, Coin amount, boolean isSend) {
        this.txId = txId;
        this.time = time;
        this.receiver = receiver;
        this.status = status;
        this.confirmNum = confirmNum;
        this.fee = fee;
        this.amount = amount;
        this.isSend = isSend;
    }

    public static TransactionWrapper from(Transaction tx, Wallet wallet){
        Sha256Hash txId = tx.getTxId();
        Date time = tx.getUpdateTime();
        Address receiver = WalletUtil.getSentAddress(tx, wallet);
        TransactionConfidence confidence = tx.getConfidence();
        Integer confirmNum = confidence.getDepthInBlocks();
        TransactionConfidence.ConfidenceType status = confidence.getConfidenceType();
        boolean isSend = true;
        Coin fee=tx.getFee();
        if(fee==null){
            fee=Coin.ZERO;
        }
        Coin amount = tx.getValueSentFromMe(wallet);
        if(amount.isZero()){
            amount = tx.getValueSentToMe(wallet);
            isSend = false;
        }
//        double amountStr = amount.toBtc().doubleValue();
        return new TransactionWrapper(txId, time, receiver, status, confirmNum, fee, amount, isSend);
    }

    public Coin getFee() {
        return fee;
    }

    public Date getTime() {
        return time;
    }

    public Address getReceiver() {
        return receiver;
    }

    public TransactionConfidence.ConfidenceType getStatus() {
        return status;
    }

    public Integer getConfirmNum() {
        return confirmNum;
    }

    public Coin getAmount() {
        return amount;
    }

    public String getAmountString(){
        return amount.toFriendlyString();
    }

    public boolean isSend() {
        return isSend;
    }

    public Sha256Hash getTxId() {
        return txId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionWrapper that = (TransactionWrapper) o;
        return txId.equals(that.txId);
    }

    @Override
    public int hashCode() {
        return txId.hashCode();
    }

    public static final Comparator<TransactionWrapper> SORT_BY_UPDATE_TIME = new Comparator<TransactionWrapper>() {
        @Override
        public int compare(final TransactionWrapper tx1, final TransactionWrapper tx2) {
            final long time1 = tx1.getTime().getTime();
            final long time2 = tx2.getTime().getTime();
            final int updateTimeComparison = -(Long.compare(time1, time2));
            //If time1==time2, compare by tx hash to make comparator consistent with equals
            return updateTimeComparison != 0 ? updateTimeComparison : tx1.getTxId().compareTo(tx2.getTxId());
        }
    };
}

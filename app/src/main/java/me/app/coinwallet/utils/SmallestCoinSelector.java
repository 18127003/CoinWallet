package me.app.coinwallet.utils;

import org.bitcoinj.core.*;
import org.bitcoinj.wallet.CoinSelection;
import org.bitcoinj.wallet.CoinSelector;

import java.math.BigInteger;
import java.util.*;

public class SmallestCoinSelector implements CoinSelector {
    private static SmallestCoinSelector instance;

    @Override
    public CoinSelection select(Coin target, List<TransactionOutput> candidates) {
        List<TransactionOutput> selected = new ArrayList<>();
        List<TransactionOutput> sorted = new ArrayList<>(candidates);
        if (!target.equals(NetworkParameters.MAX_MONEY)) {
            sort(sorted);
        }
        long total = 0;
        for (TransactionOutput output : sorted) {
            if (total >= target.value) {
                break;
            }
            if (!shouldSelect(output.getParentTransaction())) {
                continue;
            }
            selected.add(output);
            total += output.getValue().value;
        }

        return new CoinSelection(Coin.valueOf(total), selected);
    }

    private static void sort(List<TransactionOutput> outputs){
        outputs.sort((o1, o2) -> {
            Coin value1 = o1.getValue();
            Coin value2 = o2.getValue();
            int r1 = value1.compareTo(value2);
            if (r1 != 0){
                return r1;
            }
            Integer depth1 = o1.getParentTransactionDepthInBlocks();
            Integer depth2 = o2.getParentTransactionDepthInBlocks();
            int r2 = depth1.compareTo(depth2);
            if (r2 != 0){
                return r2;
            }
            BigInteger aHash = o1.getParentTransactionHash().toBigInteger();
            BigInteger bHash = o2.getParentTransactionHash().toBigInteger();
            return aHash.compareTo(bHash);
        });
    }

    protected boolean shouldSelect(Transaction tx) {
        if (tx != null) {
            return isSelectable(tx);
        }
        return true;
    }

    public static boolean isSelectable(Transaction tx) {
        // Only pick chain-included transactions, or transactions that are ours and building.
        TransactionConfidence confidence = tx.getConfidence();
        TransactionConfidence.ConfidenceType type = confidence.getConfidenceType();
        return type.equals(TransactionConfidence.ConfidenceType.BUILDING) ||
                type.equals(TransactionConfidence.ConfidenceType.PENDING) &&
                confidence.getSource().equals(TransactionConfidence.Source.SELF) &&
                (confidence.numBroadcastPeers() > 0 || tx.getParams().getId().equals(NetworkParameters.ID_REGTEST));
    }


    public static SmallestCoinSelector get(){
        if (instance == null){
            instance = new SmallestCoinSelector();
        }
        return instance;
    }
}

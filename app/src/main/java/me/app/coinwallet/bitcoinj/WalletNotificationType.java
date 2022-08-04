package me.app.coinwallet.bitcoinj;

public enum WalletNotificationType {
    SETUP_COMPLETED,
    SYNC_COMPLETED,
    SYNC_PROGRESS,
    SYNC_STARTED,
    BALANCE_CHANGED,
    TX_RECEIVED,
    TX_ACCEPTED,
    TX_BROADCAST_COMPLETED,
    SYNC_STOPPED,
    ACCOUNT_ADDED
}

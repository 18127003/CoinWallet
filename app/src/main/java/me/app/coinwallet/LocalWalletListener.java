package me.app.coinwallet;

public interface LocalWalletListener {
    void update(WalletNotificationType type, String content);
}

package me.app.coinwallet.exceptions;

public class MnemonicInaccessibleException extends RuntimeException{
    public MnemonicInaccessibleException() {
        super("Wallet need to be decrypted to extract mnemonic");
    }

    public MnemonicInaccessibleException(String message) {
        super(message);
    }

    public MnemonicInaccessibleException(String message, Throwable cause) {
        super(message, cause);
    }
}

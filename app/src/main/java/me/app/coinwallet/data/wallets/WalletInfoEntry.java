package me.app.coinwallet.data.wallets;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = WalletInfoEntry.TABLE_NAME)
public class WalletInfoEntry {
    public static final String TABLE_NAME = "wallet_info";

    @NonNull
    @ColumnInfo(name = "label")
    private String label;

    @ColumnInfo(name = "type")
    private String type;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "account")
    private Integer accountIndex;

    public WalletInfoEntry(final int accountIndex, @NonNull final String label, final String type) {
        this.accountIndex = accountIndex;
        this.label = label;
        this.type = type;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    public String getType() { return type; }

    @NonNull
    public Integer getAccountIndex(){return accountIndex;}
}

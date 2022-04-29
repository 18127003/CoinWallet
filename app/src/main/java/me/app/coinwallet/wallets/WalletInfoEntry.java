package me.app.coinwallet.wallets;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = WalletInfoEntry.TABLE_NAME)
public class WalletInfoEntry {
    public static final String TABLE_NAME = "wallet_info";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "label")
    private String label;

    @ColumnInfo(name = "type")
    private String type;

    public WalletInfoEntry(final String label, final String type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public String getType() { return type; }
}

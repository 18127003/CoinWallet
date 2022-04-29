package me.app.coinwallet.wallets;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { WalletInfoEntry.class }, version = 1, exportSchema = false)
public abstract class WalletInfoDatabase extends RoomDatabase {
    public abstract WalletInfoDao walletInfoDao();

    private static final String DATABASE_NAME = "wallet_info";
    private static WalletInfoDatabase INSTANCE;

    public static WalletInfoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WalletInfoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), WalletInfoDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}

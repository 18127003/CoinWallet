package me.app.coinwallet.marketcap;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { MarketCapEntry.class }, version = 1, exportSchema = false)
public abstract class MarketCapDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "market_caps";
    private static MarketCapDatabase INSTANCE;

    public static MarketCapDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MarketCapDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MarketCapDatabase.class,
                            DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract MarketCapDao marketCapDao();
}

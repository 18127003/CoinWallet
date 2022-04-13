package me.app.coinwallet.addressbook;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { AddressBookEntry.class }, version = 1, exportSchema = false)
public abstract class AddressBookDatabase extends RoomDatabase {
    public abstract AddressBookDao addressBookDao();

    private static final String DATABASE_NAME = "address_book";
    private static AddressBookDatabase INSTANCE;

    public static AddressBookDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AddressBookDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AddressBookDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}

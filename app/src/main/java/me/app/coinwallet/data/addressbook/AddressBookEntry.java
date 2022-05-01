package me.app.coinwallet.data.addressbook;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = AddressBookEntry.TABLE_NAME)
public class AddressBookEntry {
    public static final String TABLE_NAME = "address_book";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "label")
    private String label;

    public AddressBookEntry(final String address, final String label) {
        this.address = address;
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public String getLabel() {
        return label;
    }

    public static Map<String, AddressBookEntry> asMap(final List<AddressBookEntry> entries) {
        if (entries == null)
            return null;
        final Map<String, AddressBookEntry> addressBook = new HashMap<>();
        for (final AddressBookEntry entry : entries)
            addressBook.put(entry.getAddress(), entry);
        return addressBook;
    }
}

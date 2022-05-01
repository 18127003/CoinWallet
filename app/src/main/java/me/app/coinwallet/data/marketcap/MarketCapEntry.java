package me.app.coinwallet.data.marketcap;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.util.Date;

@Entity(tableName = MarketCapEntry.TABLE_NAME, indices = { @Index(value = { "source", "name" },
        unique = true) })
public final class MarketCapEntry {
    public static final String TABLE_NAME = "market_caps";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "source")
    private String source;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "cap_timestamp")
    @TypeConverters({ DateConverters.class })
    private Date capTimeStamp;

    @ColumnInfo(name = "current_price")
    private double currentPrice;

    @ColumnInfo(name = "market_cap")
    private long marketCap;

    @ColumnInfo(name = "market_cap_rank")
    private int marketCapRank;

    public MarketCapEntry(long id, @NonNull String source, @NonNull String name, @NonNull Date capTimeStamp,
                          double currentPrice, long marketCap, int marketCapRank) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.capTimeStamp = capTimeStamp;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
    }

    public MarketCapEntry(final String source, final MarketCapJson marketCapJson) {
        this.source = source;
        this.name = marketCapJson.name;
        this.currentPrice = marketCapJson.current_price;
        this.capTimeStamp = new Date();
        this.marketCap = marketCapJson.market_cap;
        this.marketCapRank = marketCapJson.market_cap_rank;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getSource() {
        return source;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public Date getCapTimeStamp() {
        return capTimeStamp;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public int getMarketCapRank() {
        return marketCapRank;
    }

    //    @Override
//    public String toString() {
//        final StringBuilder builder = new StringBuilder();
//        builder.append(getClass().getSimpleName());
//        builder.append('[');
//        builder.append(fiat().toFriendlyString());
//        builder.append(" per ");
//        builder.append(coin().toFriendlyString());
//        builder.append(']');
//        return builder.toString();
//    }

    public static final class DateConverters {
        @TypeConverter
        public static Date millisToDate(final long millis) {
            return new Date(millis);
        }

        @TypeConverter
        public static long dateToMillis(final Date date) {
            return date.getTime();
        }
    }
}

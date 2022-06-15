package me.app.coinwallet.data.marketcap;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.*;
import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity(tableName = MarketCapEntry.TABLE_NAME,
        indices = { @Index(value = { "source", "name" }, unique = true) }
        )
public final class MarketCapEntry implements Parcelable {
    public static final String TABLE_NAME = "market_caps";

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "coin_id")
    private String coinId;

    @NonNull
    @ColumnInfo(name = "source")
    private String source;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "symbol")
    private String symbol;

    @NonNull
    @ColumnInfo(name = "price_change_percentage_24h")
    private Float fluctuation;

    @NonNull
    @ColumnInfo(name = "high_24h")
    Double high;

    @NonNull
    @ColumnInfo(name = "low_24h")
    Double low;

    @NonNull
    @ColumnInfo(name = "total_volume")
    Double totalVolume;

    @NonNull
    @ColumnInfo(name = "image")
    String image;

    @ColumnInfo(name = "current_price")
    private final double currentPrice;

    @ColumnInfo(name = "market_cap")
    private final long marketCap;

    @ColumnInfo(name = "market_cap_rank")
    private final int marketCapRank;

    @ColumnInfo(name = "chart")
    private final List<Entry> chart;

    public MarketCapEntry(@NonNull Long id, @NonNull String coinId, @NonNull String source, @NonNull String name, @NonNull String symbol,
                          @NonNull Float fluctuation, @NonNull Double high, @NonNull Double low,
                          @NonNull Double totalVolume, @NonNull String image,
                          double currentPrice, long marketCap, int marketCapRank, List<Entry> chart) {
        this.id = id;
        this.coinId=coinId;
        this.source = source;
        this.name = name;
        this.symbol = symbol;
        this.fluctuation = fluctuation;
        this.high = high;
        this.low = low;
        this.totalVolume = totalVolume;
        this.image = image;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.chart = chart;
    }

    public MarketCapEntry(final String source, final MarketCapJson marketCapJson) {
        Log.e("HD",marketCapJson.toString());
        this.source = source;
        this.name = marketCapJson.name;
        this.currentPrice = marketCapJson.currentPrice;
        this.marketCap = marketCapJson.marketCapValue;
        this.marketCapRank = marketCapJson.marketCapRank;
        this.symbol=marketCapJson.symbol;
        this.fluctuation=marketCapJson.fluctuation;
        this.high=marketCapJson.high;
        this.low=marketCapJson.low;
        this.totalVolume=marketCapJson.totalVolume;
        this.image=marketCapJson.image;
        this.id=marketCapJson.id;
        this.chart=chartFromJson(marketCapJson.chartDto);
        this.coinId=marketCapJson.coinId;
    }

    protected MarketCapEntry(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        coinId = in.readString();
        source = in.readString();
        name = in.readString();
        symbol = in.readString();
        if (in.readByte() == 0) {
            fluctuation = null;
        } else {
            fluctuation = in.readFloat();
        }
        if (in.readByte() == 0) {
            high = null;
        } else {
            high = in.readDouble();
        }
        if (in.readByte() == 0) {
            low = null;
        } else {
            low = in.readDouble();
        }
        if (in.readByte() == 0) {
            totalVolume = null;
        } else {
            totalVolume = in.readDouble();
        }
        image = in.readString();
        currentPrice = in.readDouble();
        marketCap = in.readLong();
        marketCapRank = in.readInt();
        chart = in.createTypedArrayList(Entry.CREATOR);
    }

    public static final Creator<MarketCapEntry> CREATOR = new Creator<MarketCapEntry>() {
        @Override
        public MarketCapEntry createFromParcel(Parcel in) {
            return new MarketCapEntry(in);
        }

        @Override
        public MarketCapEntry[] newArray(int size) {
            return new MarketCapEntry[size];
        }
    };

    private List<Entry> chartFromJson(MarketChartJson json){
        return json.pointList.stream().map(item->{
            Float a = Float.valueOf(item.get(0));
            Float b = Float.valueOf(item.get(1));
            return new Entry(a,b);
        }).collect(Collectors.toList());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(coinId);
        parcel.writeString(source);
        parcel.writeString(name);
        parcel.writeString(symbol);
        if (fluctuation == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(fluctuation);
        }
        if (high == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(high);
        }
        if (low == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(low);
        }
        if (totalVolume == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(totalVolume);
        }
        parcel.writeString(image);
        parcel.writeDouble(currentPrice);
        parcel.writeLong(marketCap);
        parcel.writeInt(marketCapRank);
        parcel.writeTypedList(chart);
    }

    public static final class EntryConverters {
        @TypeConverter
        public static String entryToString(final List<Entry> entries) {
            return entries.stream().map(entry -> entry.getX()+":"+entry.getY()).reduce((a,b)->a+","+b).orElse("");
        }

        @TypeConverter
        public static List<Entry> stringToEntry(final String string) {
            List<String> strings= Arrays.asList(string.split(","));
            return strings.stream().map(s->{
                String[] item=s.split(":");
                return new Entry(Float.parseFloat(item[0]),Float.parseFloat(item[1]));
            }).collect(Collectors.toList());

        }
    }

    @NonNull
    public String getCoinId() {
        return coinId;
    }

    @NonNull
    public List<Entry> getChart() {
        return chart;
    }

    @NonNull
    public Float getFluctuation() {
        return fluctuation;
    }

    @NonNull
    public Double getHigh() {
        return high;
    }

    @NonNull
    public Double getLow() {
        return low;
    }

    @NonNull
    public Double getTotalVolume() {
        return totalVolume;
    }

    @NonNull
    public String getImage() {
        return image;
    }

    @NonNull
    public Long getId() {
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

    public double getCurrentPrice() {
        return currentPrice;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public int getMarketCapRank() {
        return marketCapRank;
    }

    @NonNull
    public String getSymbol() {
        return symbol;
    }

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

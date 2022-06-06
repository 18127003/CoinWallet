package me.app.coinwallet.data.marketcap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.*;
import com.github.mikephil.charting.data.Entry;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity(tableName = MarketCapEntry.TABLE_NAME,
        indices = { @Index(value = { "source", "name" }, unique = true) },
        foreignKeys = {@ForeignKey(entity = MarketCapEntry.class,parentColumns = "id",
        childColumns = "id",onDelete = ForeignKey.CASCADE)})
public final class MarketCapEntry {
    public static final String TABLE_NAME = "market_caps";

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

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


    @NonNull
    @ColumnInfo(name = "cap_timestamp")
    @TypeConverters({ DateConverters.class })
    private final Date capTimeStamp;

    @ColumnInfo(name = "current_price")
    private final double currentPrice;

    @ColumnInfo(name = "market_cap")
    private final long marketCap;

    @ColumnInfo(name = "market_cap_rank")
    private final int marketCapRank;

    @ColumnInfo(name = "chart")
    private final List<Entry> chart;

    public MarketCapEntry(@NonNull String id, @NonNull String source, @NonNull String name, @NonNull String symbol,
                          @NonNull Float fluctuation, @NonNull Double high, @NonNull Double low,
                          @NonNull Double totalVolume, @NonNull String image, @NonNull Date capTimeStamp,
                          double currentPrice, long marketCap, int marketCapRank, List<Entry> chart) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.symbol = symbol;
        this.fluctuation = fluctuation;
        this.high = high;
        this.low = low;
        this.totalVolume = totalVolume;
        this.image = image;
        this.capTimeStamp = capTimeStamp;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.chart = chart;
    }

    public MarketCapEntry(final String source, final MarketCapJson marketCapJson) {
        this.source = source;
        this.name = marketCapJson.name;
        this.currentPrice = marketCapJson.currentPrice;
        this.capTimeStamp = new Date();
        this.marketCap = marketCapJson.marketCap;
        this.marketCapRank = marketCapJson.marketCapRank;
        this.symbol=marketCapJson.symbol;
        this.fluctuation=marketCapJson.fluctuation;
        this.high=marketCapJson.high;
        this.low=marketCapJson.low;
        this.totalVolume=marketCapJson.totalVolume;
        this.image=marketCapJson.image;
        this.id=marketCapJson.id;
        this.chart=chartFromJson(marketCapJson.chartDto);
    }

    private List<Entry> chartFromJson(List<List<String>> json){
        return json.stream().map(item->{
            Float a = Float.valueOf(item.get(0));
            Float b = Float.valueOf(item.get(1));
            return new Entry(a,b);
        }).collect(Collectors.toList());
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
    public String getId() {
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

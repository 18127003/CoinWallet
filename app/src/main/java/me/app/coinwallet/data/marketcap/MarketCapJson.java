package me.app.coinwallet.data.marketcap;

import java.util.List;

public class MarketCapJson {
    public String id;
    public String symbol;
    public String name;
    public String image;
    public Double currentPrice;
    public Long marketCap;
    public Integer marketCapRank;
    public Double totalVolume;
    public Double high;
    public Double low;
    public Float fluctuation;
    public List<List<String>> chartDto;
}

package me.app.coinwallet.data.marketcap;

import androidx.core.util.Pair;

import java.io.Serializable;
import java.util.List;

public class MarketChartJson {
    public List<List<String>> pointList;

    @Override
    public String toString() {
        return "MarketChartJson{" +
                "chartDto=" + pointList +
                '}';
    }
}

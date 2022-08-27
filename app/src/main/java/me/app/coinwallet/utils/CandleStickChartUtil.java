package me.app.coinwallet.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CandleStickChartUtil {

    private CandleStickChart candleStickChart;
    private final MarketCapEntity cap;
    private final Resources res;
    private Description description;
    private List<CandleEntry> candleEntries = new ArrayList<>();
    public CandleStickChartUtil(MarketCapEntity cap, Resources res){
        this.cap = cap;
        this.res = res;
    }
    public CandleStickChartUtil chart(CandleStickChart chart){
        this.candleStickChart = chart;
        return this;
    }

    public void visualize(boolean drawValue,@ColorInt int color){
        customCandleStickChart(drawValue,color);
    }

    public CandleStickChartUtil description(@ColorInt int color){
        description=new Description();
        description.setTextColor(color);
        description.setText(cap.getName());
        description.setTextSize(16);
        return this;
    }

    public CandleStickChartUtil disableAxis(){
        candleStickChart.getAxisLeft().setDrawLabels(false);
        candleStickChart.getAxisLeft().setDrawAxisLine(false);
        candleStickChart.getAxisLeft().setDrawGridLines(false);
        candleStickChart.getAxisRight().setDrawAxisLine(false);
        candleStickChart.getXAxis().setDrawAxisLine(false);
        return this;
    }

    public CandleStickChartUtil disableTouch(){
        candleStickChart.setDragEnabled(false);
        candleStickChart.setTouchEnabled(false);
        return this;
    }

    List<CandleEntry> convertEntry(){
        List<CandleEntry> entries=new ArrayList<>();
        for (int i = 0; i < cap.getChart().size()/24; i++) {
            List<Entry> capDaily=cap.getChart().subList(i*24,i*24+24);
            float high= capDaily.stream().map(BaseEntry::getY).max(Float::compare).orElse(0F);
            float low= capDaily.stream().map(BaseEntry::getY).min(Float::compare).orElse(0F);
            entries.add(new CandleEntry(i,high,low,capDaily.get(0).getY(),capDaily.get(capDaily.size()-1).getY()));
        }
        return entries;
    }
    CandleDataSet createDataSet(boolean drawValue, @ColorInt int color){
        candleEntries=convertEntry();
        CandleDataSet set1 = new CandleDataSet(candleEntries, "DataSet 1");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(res.getColor(R.color.grey));
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(res.getColor(R.color.red));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(res.getColor(R.color.old_green));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(drawValue);
        set1.setValueTextColor(color);
        return set1;

    }

    void customCandleStickChart(boolean drawValue, @ColorInt int color){
        candleStickChart.setHighlightPerDragEnabled(true);

        candleStickChart.setDrawBorders(false);

//        candleStickChart.setBorderColor(res.getColor(R.color.grey));

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();

        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(color);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);

        candleStickChart.setDescription(description);
        CandleData data = new CandleData(createDataSet(drawValue,color));
        candleStickChart.setData(data);
        candleStickChart.invalidate();
    }

}

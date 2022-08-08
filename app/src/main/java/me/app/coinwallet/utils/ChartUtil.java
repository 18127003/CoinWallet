package me.app.coinwallet.utils;

import android.content.res.Resources;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.color.MaterialColors;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapEntity;


public class ChartUtil {
    private final MarketCapEntity cap;
    private final Resources res;
    private LineChart chart;
    private Description description;

    public ChartUtil(MarketCapEntity cap, Resources res){
        this.cap = cap;
        this.res = res;
    }

    public void visualize(){
        configChart();
    }

    public ChartUtil chart(LineChart chart){
        this.chart = chart;
        return this;
    }

    public ChartUtil axisColor(@ColorInt int color){
        chart.getAxisLeft().setTextColor(color);
        return this;
    }

    public ChartUtil disableAxis(){
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        return this;
    }

    public ChartUtil disableTouch(){
        chart.setDragEnabled(false);
        chart.setTouchEnabled(false);
        return this;
    }

    private void configChart(){
        chart.setBackgroundColor(res.getColor(R.color.zxing_transparent));
        chart.setDescription(description);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        chart.setClickable(false);
        chart.setData(new LineData(getData()));
    }

    public ChartUtil description(){
        description=new Description();
        description.setText(cap.getName());
        description.setTextSize(16);
        return this;
    }

    public ChartUtil descriptionColor(@ColorInt int color){
        description.setTextColor(color);
        return this;
    }

    public ChartUtil disableDescription(){
        description = new Description();
        description.setEnabled(false);
        return this;
    }

    private LineDataSet getData(){
        LineDataSet lineDataSet=new LineDataSet(cap.getChart(), cap.getName());
        lineDataSet.setValueTextColor(res.getColor(R.color.white));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(true);
        if(cap.getFluctuation()>=0){
            lineDataSet.setColor(res.getColor(R.color.old_green));
        }
        else {
            lineDataSet.setColor(res.getColor(R.color.red));
        }
        return lineDataSet;
    }
}

package me.app.coinwallet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        reader.close();
        return sb.toString();
    }

    public static String getMonthYearFromDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return (month+1)+"/"+year;
    }


    public static boolean hasInternetAccess(Context ctx){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null){
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        final boolean wifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        final boolean cellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        final boolean ethernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        return wifi || cellular || ethernet;
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) {
        return string.regionMatches(true, 0, prefix, 0, prefix.length());

    public static String formatDate(Date date){
        try {
            return format.format(date);
        }
        catch (Exception e){
            return null;
        }

    }
}

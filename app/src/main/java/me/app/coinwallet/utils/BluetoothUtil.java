package me.app.coinwallet.utils;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.UUID;

public class BluetoothUtil {
    /** Used for local fetching of BIP70 payment requests. */
    public static final UUID PAYMENT_REQUESTS_UUID = UUID.fromString("3357A7BB-762D-464A-8D9A-DCA592D57D59");

    /** Used for talking the deprecated pre-BIP70 payment protocol. */
    public static final UUID CLASSIC_PAYMENT_PROTOCOL_UUID = UUID.fromString("3357A7BB-762D-464A-8D9A-DCA592D57D5B");
    public static final String CLASSIC_PAYMENT_PROTOCOL_NAME = "Bitcoin classic payment protocol (deprecated)";
    /** This URI parameter holds the MAC address for the deprecated pre-BIP70 payment protocol. */
    public static final String MAC_URI_PARAM = "bt";
    /** Android 6 uses this MAC address instead of the real one. */
    private static final String MARSHMALLOW_FAKE_MAC = "02:00:00:00:00:00";


    public static @Nullable
    String getAddress(final BluetoothAdapter adapter) {
        if (adapter == null)
            return null;
//        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
//            final String address = adapter.getAddress();
//            if (!MARSHMALLOW_FAKE_MAC.equals(address))
//                return address;
//        }

        // Horrible reflection hack needed to get the Bluetooth MAC for Marshmallow and above.
        try {
            final Field mServiceField = BluetoothAdapter.class.getDeclaredField("mService");
            mServiceField.setAccessible(true);
            final Object mService = mServiceField.get(adapter);
            if (mService == null)
                return null;
            return (String) mService.getClass().getMethod("getAddress").invoke(mService);
        } catch (final InvocationTargetException x) {
            Log.e("HD","Problem determining Bluetooth MAC via reflection");
            x.printStackTrace();
            return null;
        } catch (final Exception x) {
            throw new RuntimeException(x);
        }
    }

    public static String compressMac(final String decompressedMac) throws IllegalArgumentException {
        final StringBuilder compressedMac = new StringBuilder();
        for (final CharSequence segment : Splitter.on(':').split(decompressedMac)) {
            if (segment.length() > 2)
                throw new IllegalArgumentException("Over-sized segment in: " + decompressedMac);
            for (int i = 0; i < segment.length(); i++) {
                final char c = segment.charAt(i);
                if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F'))
                    throw new IllegalArgumentException("Illegal character '" + c + "' in: " + decompressedMac);
            }
            compressedMac.append(Strings.padStart(segment.toString(), 2, '0').toUpperCase(Locale.US));
        }
        return compressedMac.toString();
    }

    public static String decompressMac(final String compressedMac) throws IllegalArgumentException {
        if (compressedMac.length() % 2 != 0)
            throw new IllegalArgumentException("Impossible length: " + compressedMac);
        final StringBuilder decompressedMac = new StringBuilder();
        for (int i = 0; i < compressedMac.length(); i++) {
            final char c = compressedMac.charAt(i);
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F'))
                throw new IllegalArgumentException("Illegal character '" + c + "' in: " + compressedMac);
            if (i % 2 == 0 && decompressedMac.length() > 0)
                decompressedMac.append(':');
            decompressedMac.append(Character.toUpperCase(c));
        }
        return decompressedMac.toString();
    }

    public static boolean isBluetoothUrl(final String url) {
        return url != null && Utils.startsWithIgnoreCase(url, "bt:");
    }

    public static String getBluetoothMac(final String url) {
        if (!isBluetoothUrl(url))
            throw new IllegalArgumentException(url);

        final int queryIndex = url.indexOf('/');
        if (queryIndex != -1)
            return url.substring(3, queryIndex);
        else
            return url.substring(3);
    }

    public static String getBluetoothQuery(final String url) {
        if (!isBluetoothUrl(url))
            throw new IllegalArgumentException(url);

        final int queryIndex = url.indexOf('/');
        if (queryIndex != -1)
            return url.substring(queryIndex);
        else
            return "/";
    }
}

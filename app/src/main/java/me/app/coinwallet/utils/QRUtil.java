package me.app.coinwallet.utils;

import android.graphics.Bitmap;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.ScanOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class QRUtil {
    private final static QRCodeWriter QR_CODE_WRITER = new QRCodeWriter();
    private static final Logger log = LoggerFactory.getLogger(QRUtil.class);

    public static Bitmap createQRCodeBitmap(final String content) {
        try {
            final Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            final BitMatrix result = QR_CODE_WRITER.encode(content, BarcodeFormat.QR_CODE, 0, 0, hints);

            final int width = result.getWidth();
            final int height = result.getHeight();
            final byte[] pixels = new byte[width * height];

            for (int y = 0; y < height; y++) {
                final int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = (byte) (result.get(x, y) ? -1 : 0);
                }
            }

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(pixels));
            return bitmap;
        } catch (final WriterException x) {
            return null;
        }
    }
}

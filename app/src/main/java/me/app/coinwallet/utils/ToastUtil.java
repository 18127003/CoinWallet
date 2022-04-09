package me.app.coinwallet.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class ToastUtil {
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ToastUtil(final Context context) {
        this.context = context;
    }

    /***
     * Create a toast
     * @param textResId string text id in resource
     * @param duration android.widget.Toast.LENGTH_SHORT or android.widget.Toast.LENGTH_LONG
     */
    public final void postToast(final int textResId, final int duration, final Object... formatArgs) {
        handler.post(() -> toast(textResId, duration, formatArgs));
    }

    public final void toast(final int textResId, final int duration, final Object... formatArgs) {
        customToast(textResId, duration, formatArgs);
    }

    /***
     * Create a toast
     * @param text content in string
     * @param duration android.widget.Toast.LENGTH_SHORT or android.widget.Toast.LENGTH_LONG
     */
    public final void postToast(final CharSequence text, final int duration) {
        handler.post(() -> toast(text, duration));
    }

    public final void toast(final CharSequence text, final int duration) {
        customToast(text, duration);
    }

    private void customToast(final int textResId, final int duration, final Object... formatArgs) {
        customToast(context.getString(textResId, formatArgs), duration);
    }

    private void customToast(final CharSequence text, final int duration) {
        android.widget.Toast.makeText(context, text, duration).show();
    }
}

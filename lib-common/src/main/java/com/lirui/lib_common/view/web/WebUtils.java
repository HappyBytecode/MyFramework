package com.lirui.lib_common.view.web;

import android.os.Build;
import android.os.Looper;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/28
 *      desc    :
 *      version : 1.0
 *  </pre>
 */

public class WebUtils {

    /**
     * 检查SDK版本是否 >= 3.0 (API 11)
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 检查SDK版本是否 >= 4.2 (API 17)
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static final void clearWebView(WebView m) {

        if (m == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        m.loadUrl("about:blank");
        m.stopLoading();
        if (m.getHandler() != null)
            m.getHandler().removeCallbacksAndMessages(null);
        m.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) m.getParent())) != null)
            mViewGroup.removeView(m);
        m.setWebChromeClient(null);
        m.setWebViewClient(null);
        m.setTag(null);
        m.clearHistory();
        m.destroy();
        m = null;
    }
}

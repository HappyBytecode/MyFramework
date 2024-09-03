package com.lirui.lib_common.view.web.listener;

import android.graphics.Bitmap;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/28
 *      desc    : 网页加载进度监听
 *      version : 1.0
 *  </pre>
 */

public interface IWebLoadListener {
    void loadStart();

    void loadFinish();

    void loadError();

    void loadProgress(int nowProgress);

    void loadTile(String title);

    void loadIcom(Bitmap icon);

}

package com.lirui.lib_common.view.web.progress;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/24
 *      desc    :
 *      version : 1.0
 *  </pre>
 */

public interface OnWebProgressListener {
    void reset();

    void show();

    void hide();

    void setProgress(int newProgress);
}

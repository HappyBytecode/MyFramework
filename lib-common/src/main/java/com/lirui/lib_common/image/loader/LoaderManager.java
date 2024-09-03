package com.lirui.lib_common.image.loader;

/**
 * 图片加载管理，可定制图片加载框架
 */
public class LoaderManager {
    private static ILoader loader;

    public static ILoader getLoader() {
        if (loader == null) {
            synchronized (LoaderManager.class) {
                if (loader == null) {
                    loader = new GlideLoader();
                }
            }
        }
        return loader;
    }
}

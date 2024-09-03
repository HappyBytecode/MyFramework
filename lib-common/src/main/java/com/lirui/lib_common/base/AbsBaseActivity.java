package com.lirui.lib_common.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;

import java.util.List;

/**
 * 基类Activity
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    protected static String TAG = null;// Log tag
    protected Context mContext = null;//context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentViewID());
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mContext = this;
        TAG = this.getClass().getSimpleName();
        BaseAppManager.getInstance().addActivity(this);
        getExtra(getIntent());
        initViewsAndEvents(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getExtra(intent);
    }

    /**
     * bind layout resource file
     */
    protected abstract int getContentViewID();

    /**
     * Intent data
     */
    protected void getExtra(Intent intent) {
    }

    /**
     * init views and events here
     */
    protected abstract void initViewsAndEvents(Bundle savedInstanceState);

    /**
     * 显示或更换Fragment
     *
     * @param fragmentClass   Fragment.class
     * @param containerViewId Fragment显示的空间ID
     * @param replace         是否替换
     */
    public void toogleFragment(Class<? extends Fragment> fragmentClass, String tag,
                               int containerViewId, boolean replace) {
        FragmentManager manager = getSupportFragmentManager();
        if (TextUtils.isEmpty(tag)) {
            tag = fragmentClass.getName();
        }
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);

        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
                if (replace) {
                    transaction.replace(containerViewId, fragment, tag);
                } else {
                    // 替换时保留Fragment,以便复用
                    transaction.add(containerViewId, fragment, tag);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        // 遍历存在的Fragment,隐藏其他Fragment
        List<Fragment> fragments = manager.getFragments();
        if (fragments != null)
            for (Fragment fm : fragments) {
                transaction.hide(fm);
            }
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseAppManager.getInstance().removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
        BaseAppManager.getInstance().removeActivity(this);
    }
}

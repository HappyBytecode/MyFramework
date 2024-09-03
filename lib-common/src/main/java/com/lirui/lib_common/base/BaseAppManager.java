
package com.lirui.lib_common.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Create By Anthony on 2016/1/15
 * Class Note:
 * use {@link LinkedList} to manage your activity stack
 */
public class BaseAppManager {

    private static Stack<Activity> activityStack;
    private static BaseAppManager instance;

    private BaseAppManager() {
    }

    public static BaseAppManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                if (instance == null) {
                    instance = new BaseAppManager();
                }
            }
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    public boolean isActivityExist(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        if (activityStack == null) return;
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void finishAllActivity(Activity exceptAct) {
        while (!activityStack.isEmpty()) {
            Activity act = (Activity) activityStack.pop();
            if (act != exceptAct) {
                act.finish();
            }
        }
        activityStack.push(exceptAct);
    }

    public void appExit(Context context) {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int size() {
        return activityStack.size();
    }


    public boolean isAppExit() {
        return activityStack == null || activityStack.isEmpty();
    }
}

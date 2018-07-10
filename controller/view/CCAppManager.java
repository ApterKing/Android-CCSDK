package cc.sdkutil.controller.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Message;
import android.support.v4.app.Fragment;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import cc.sdkutil.controller.core.CCAppAsyncHandler;

/**
 * Created by wangcong on 14-12-18.
 * Activity 与 Fragment的封装类，将其加入栈中，方便获取Activity与Fragment和彻底退出程序. <br>
 */
public class CCAppManager {

	private  static CCAppManager instance = null;

    /**
     * 用于记录当前所有压入栈的activity
     */
	private final Stack<Activity>  mActivityStack;

    /**
     * 用于记录某个Activity所持有的Fragment
     */
	private final ConcurrentHashMap<Activity, Stack<Fragment>> mFragmentsMap;

	private CCAppManager() {
        mActivityStack = new Stack<>();
        mFragmentsMap = new ConcurrentHashMap<>(0);
    }

	// 单例模式
	public synchronized static CCAppManager newInstance() {
		if (instance == null) {
            instance = new CCAppManager();
		}
		return instance;
	}

    /**
     * 添加App进入前台监听
     * @param foregroundListener
     */
    public void addAppForegroundListener(CCAppForegroundListener foregroundListener) {
        final Message msg = new Message();
        msg.what = CCAppAsyncHandler.APP_RUNNINGINFO_CHECK_ADD;
        msg.obj = foregroundListener;
        CCAppAsyncHandler.newInstance().sendMessage(msg);
    }

    /**
     * 移除某个指定监听
     * @param foregroundListener
     */
    public void removeAppForegroundListener(CCAppForegroundListener foregroundListener) {
        final Message msg = new Message();
        msg.what = CCAppAsyncHandler.APP_RUNNINGINFO_CHECK_REMOVE;
        msg.obj = foregroundListener;
        CCAppAsyncHandler.newInstance().sendMessage(msg);
    }

    /**
     * 添加App进入前台监听
     * @param backgroundListener
     */
    public void addAppBackgroundListener(CCAppBackgroundListener backgroundListener) {
        final Message msg = new Message();
        msg.what = CCAppAsyncHandler.APP_RUNNINGINFO_CHECK_ADD;
        msg.obj = backgroundListener;
        CCAppAsyncHandler.newInstance().sendMessage(msg);
    }

    public void removeAppBackgroundListener(CCAppBackgroundListener backgroundListener) {
        final Message msg = new Message();
        msg.what = CCAppAsyncHandler.APP_RUNNINGINFO_CHECK_REMOVE;
        msg.obj = backgroundListener;
        CCAppAsyncHandler.newInstance().sendMessage(msg);
    }

    /**
     * 获取当前程序的优先级
     * @return
     */
    public int getAppImportance() {
        return CCAppAsyncHandler.newInstance().getCurrentAppRunningInfo().importance;
    }

	/**
	 * 新增Fragment，此处必须传入Fragment所在的Activity
	 * @param activity
	 * @param fragment
	 */
	public void addFragment(Activity activity, Fragment fragment) {
		Stack<Fragment> stack = mFragmentsMap.get(activity);
		if (stack == null) {
			stack = new Stack<>();
			mFragmentsMap.put(activity, stack);
		}
		stack.push(fragment);
	}

	/**
	 * 获取最后加入指定activity中的Fragment
	 * @param activity
	 * @return
	 */
	public Fragment lastFragment(Activity activity) {
		Stack<Fragment> stack = mFragmentsMap.get(activity);
		return stack == null ? null : stack.peek();
	}

	/**
	 * 移除指定Fragment
	 * @param activity
	 * @param fragment
	 */
	public void finishFragment(Activity activity, Fragment fragment) {
		Stack<Fragment> stack = mFragmentsMap.get(activity);
		if (stack != null && fragment != null) {
            stack.removeElement(fragment);
        }
	}
	
	/**
	 *  添加activity
	 */
	public void addActivity(Activity activity){
		mActivityStack.add(activity);
	}
	
	/**
	 * 获取到当前activit栈的大小
	 * @return
	 */
	public int getActivityCount() {
		return mActivityStack.size();
	}
	
	/**
	 * 获取栈顶的activity
	 */
	public Activity lastActivity() {
        try {
            return mActivityStack.lastElement();
        } catch (NoSuchElementException e) {
            return null;
        }
	}
	
	public Activity firstActivity() {
        try {
            return mActivityStack.firstElement();
        } catch (NoSuchElementException e) {
            return null;
        }
	}
	
	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity(){
		Activity activity = lastActivity();
        finishActivity(activity);
	}
	
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity){
		if(activity != null){
			mActivityStack.remove(activity);
			activity.finish();
		}
		mFragmentsMap.remove(activity);
	}
	
	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : mActivityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity(){
		for (int i = 0, size = mActivityStack.size(); i < size; i++){
            if (null != mActivityStack.get(i)){
                mActivityStack.get(i).finish();
            }
	    }
        mActivityStack.clear();
	}
	
	/**
	 * 退出应用程序
	 */
	public void finishApp(Context context) {
		try {
			finishAllActivity();

            ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(activityManager, context.getPackageName());

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
		} catch (Exception e) {	}
	}
	
}

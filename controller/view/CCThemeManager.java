package cc.sdkutil.controller.view;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wangcong on 15-4-3.
 */
public class CCThemeManager {

    private final static String THEME_PREF_NAME = "cc_theme_name";
    private final static String THEME_KEY_NAME = "cc_theme_key";

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 保存主题名称
     */
    private final SharedPreferences mSharedPref;

    /**
     * 所有theme改变监听
     */
    private final Set<CCThemeListener> mThemeSet;

    private static CCThemeManager instance = null;

    private CCThemeManager(Context context) {
        mContext = context;
        mThemeSet = new HashSet<>(0);
        mSharedPref = context.getSharedPreferences(THEME_PREF_NAME, Context.MODE_PRIVATE);
        mSharedPref.edit().putString(THEME_KEY_NAME, context.getPackageName()).commit();
        mSharedPref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (sharedPreferences == mSharedPref && key.equals(THEME_PREF_NAME)) {
                    for (CCThemeListener themeListener : mThemeSet) {
                        themeListener.onThemeChanged(sharedPreferences.getString(key, ""));
                    }
                }
            }
        });
    }

    public synchronized static CCThemeManager newInstance(Context context) {
        if (instance == null) {
            instance = new CCThemeManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 添加主题监听
     * @param themeListener
     */
    public void registerThemeListener(CCThemeListener themeListener) {
        mThemeSet.add(themeListener);
    }

    /**
     * 移除监听
     * @param themeListener
     */
    public void unRegisterThemeListener(CCThemeListener themeListener) {
        mThemeSet.remove(themeListener);
    }

    /**
     * 设置当前主题名称
     * @param themeName
     */
    public void setCurrentThemeName(String themeName) {
        mSharedPref.edit().putString(THEME_KEY_NAME, themeName).commit();
    }
}

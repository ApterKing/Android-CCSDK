package cc.sdkutil.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import cc.sdkutil.controller.inject.CCInjectUtil;
import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.controller.view.CCAppManager;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-4-3.
 */
@CCDebug
public class CCBaseActivity extends AppCompatActivity {

    protected InputMethodManager imm;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        CCAppManager.newInstance().addActivity(this);
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onCreate");
    }

    public void setContentView(int layoutResID, int titleLayoutId) {
        if(titleLayoutId != -1) requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(layoutResID);
        if(titleLayoutId != -1) getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, titleLayoutId);
        CCInjectUtil.inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, -1);
    }

    public void setContentView(View view, int titleLayoutId) {
        if(titleLayoutId != -1) requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(view);
        if(titleLayoutId != -1) getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, titleLayoutId);
        CCInjectUtil.inject(this);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, -1);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params, int titleLayoutId) {
        if(titleLayoutId != -1) requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.setContentView(view, params);
        if(titleLayoutId != -1) getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, titleLayoutId);
        CCInjectUtil.inject(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        setContentView(view, params, -1);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onStart");
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onRestart");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onResume");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onPause");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onStop");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        CCAppManager.newInstance().finishActivity(this);
        CCLogUtil.d(CCBaseActivity.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        List<Fragment> fragments = getSupportFragmentManager().getFragments();
//        if (fragments == null) return;
//        for (Fragment fragment : fragments) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
//        CCBaseFragment fragment = (CCBaseFragment) CCAppManager.newInstance().lastFragment(this);
//        if (keyCode != KeyEvent.KEYCODE_BACK && fragment != null
//                && fragment.onKeyDown(keyCode)) return true;
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (fragment != null && fragment.onKeyDown(keyCode)) return true;
//            if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
//                getSupportFragmentManager().popBackStack();
//                CCBaseFragment fragment2 = (CCBaseFragment) CCAppManager.newInstance().lastFragment(this);
//                getSupportFragmentManager().beginTransaction().show(fragment2).commit();
//                return true;
//            }
//        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
//            moveTaskToBack(true);
//            CCLogUtil.e("keycode_home");
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            hideSoftInputMethodManager();
        }
        return super.onTouchEvent(event);
    }

    // 隐藏软键盘
    protected void hideSoftInputMethodManager() {
        if(getCurrentFocus() !=null && getCurrentFocus().getWindowToken() != null){
            if (imm == null)
                imm = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

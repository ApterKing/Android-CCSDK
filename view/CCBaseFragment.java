package cc.sdkutil.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.controller.view.CCAppManager;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by apple on 15/8/29.
 */
@CCDebug
public class CCBaseFragment extends Fragment {

    protected InputMethodManager imm;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        CCAppManager.newInstance().addFragment(activity, this);
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) getActivity().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onViewCreated");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onResume");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onPause");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        hideSoftInputIfNeed();
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onStop");
    }

    protected void hideSoftInputIfNeed() {
        if (imm.isActive()) {
            IBinder iBinder = getActivity().getCurrentFocus() == null ? null
                    : getActivity().getCurrentFocus().getWindowToken();
            if (iBinder != null)
                imm.hideSoftInputFromWindow(iBinder,
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onDestroyView");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onDestroy");
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        CCAppManager.newInstance().finishFragment(getActivity(), this);
        CCLogUtil.d(CCBaseFragment.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onDetach");
    }

    public boolean onKeyDown(int keyCode) {
        return  false;
    }
}

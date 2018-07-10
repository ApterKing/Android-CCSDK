package cc.sdkutil.controller.core;

import java.util.List;

/**
 * Created by wangcong on 14-12-20. <br>
 * 实例被观察者，指定观察者 {@link CCObserver}
 */
public class CCObservable extends CCAbsObservable<CCObserver> {

    private static CCObservable instance;

    static {
        instance = null;
    }

    private CCObservable() {}

	/**
	 * 实例一个被观察者
	 * @return
	 */
	public static synchronized CCObservable newInstance() {
		if (instance == null) {
			instance = new CCObservable();
		}
		return instance;
	}
	
	@Override
	public void notifyObserver(String category, Object... objs) {
		// TODO Auto-generated method stub
		List<CCObserver> tmpList = mObserverMap.get(category);
        if (tmpList == null) return;
		for (int i = 0; i < tmpList.size(); i++) {
			tmpList.get(i).update(category, objs);
		}
	}

}

package cc.sdkutil.controller.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wangcong on 14-12-20. <br>
 * 抽象被观察者，用于观察者模式中来注册注销观察者，支持设置观察者类型标识 <br>
 * @param <T> 自定义观察者
 */
public abstract class CCAbsObservable<T> {
	
	/** 用于记录观察者  */
	public final ConcurrentMap<String, List<T>> mObserverMap = 
			new ConcurrentHashMap<String, List<T>>(16);
	
	/**
	 * 注册观察者， 默认分类为类名
	 * @param observer
	 */
	public void registerObserver(T observer) {
		this.registerObserver(observer.getClass().getName(), observer);
	}
	
	/**
	 * 通过分类及实例注册观察者
	 * @param category
	 * @param observer
	 */
	public void registerObserver(String category, T observer) {
		if (observer == null) return;
		List<T> observerList = mObserverMap.get(category);
		if (observerList == null) {
			observerList = new ArrayList<T>();
			mObserverMap.putIfAbsent(category, observerList);
		}
		if (observerList.indexOf(observer) == -1)
			observerList.add(observer);
	}

	/**
	 * 同时注册多个观察者
	 * @param categorys
	 * @param observer
	 */
	public void registerObserver(String[] categorys, T observer) {
		for (String category : categorys) {
			registerObserver(category, observer);
		}
	}
	
	/**
	 * 不通过分类注销观察者
	 * @param observer
	 */
	public void unRegisterObserver(T observer) {
		if (observer == null) return;
		Collection<List<T>> collection = mObserverMap.values();
        if (collection == null) return;
		Iterator<List<T>> iterator = collection.iterator();
		while (iterator.hasNext()) {
			Iterator<T> iteratorList = iterator.next().iterator();
			while (iteratorList.hasNext()) {
				if (iteratorList.next() == observer) {
					iteratorList.remove();
					break;
				}
			}
		}
	}

	/**
	 * 通过分类注销某个观察者，如果指定了分类，执行效率更高
	 * @param category 观察者分类
	 * @param observer 
	 */
	public void unRegisterObserver(String category, T observer) {
		if (observer == null) return;
		List<T> observerList = mObserverMap.get(category);
        if (observerList == null) return;
		Iterator<T> iterator = observerList.iterator();
		while (iterator.hasNext()) {
			if (iterator.next() == observer) {
				iterator.remove();
				break;
			}
		}
	}

	/**
	 * 注销多个观察者
	 * @param categorys
	 * @param observer
	 */
	public void unRegisterObserver(String[] categorys, T observer) {
		for (String category : categorys) {
			unRegisterObserver(category, observer);
		}
	}

	/**
	 * 注销所有观察者
	 */
	public void unRegisterAllObserver() {
		mObserverMap.clear();
	}
	
	/**
	 * 通知所有观察者
	 * @param objs
	 */
	public void notifyAllObserver(Object... objs) {
        Set<String> keyset = mObserverMap.keySet();
        if (keyset == null) return;;
		Iterator<String> iterator = keyset.iterator();
		while (iterator.hasNext()) {
			this.notifyObserver(iterator.next(), objs);
		}
	}

	/**
	 * 通过分类发送通知
	 * @param category
	 * @param objs
	 */
	public abstract void notifyObserver(String category, Object... objs);
}

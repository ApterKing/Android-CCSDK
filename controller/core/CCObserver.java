package cc.sdkutil.controller.core;

/**
 * Created by wangcong on 14-12-19. <br>
 * 观察者.   <br>
 */
public interface CCObserver {

	void update(String category, Object... objs);
	
}

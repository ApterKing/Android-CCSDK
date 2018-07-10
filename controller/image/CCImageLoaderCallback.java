package cc.sdkutil.controller.image;

import android.graphics.Bitmap;

/**
 * Created by wangcong on 14-12-18.
 * 多图片加载反馈类, 响应onSuccess 和 onFail. <br>
 */
public class CCImageLoaderCallback {

	/**
	 * 加载成功钩子方法，由使用者重写
	 * @param bitmap  图片数据
	 * @param objs 用户传入的需要重新返回的对象
	 */
	public void onSuccess(Bitmap bitmap, Object... objs) {
		
	}
	
	/**
	 * 加载失败钩子方法，由使用者重写
	 * @param reason  加载失败原因
	 */
	public void onFail(final String reason) {
		
	}

}

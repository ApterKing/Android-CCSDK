package cc.sdkutil.controller.net;

import cc.sdkutil.model.net.CCHttpException;
import cc.sdkutil.model.net.CCHttpResponseInfo;

/**
 *  Created by wangcong on 14-12-22. <br>
 *  请求响应回调， 支持success、fail、progress响应. <br>
 */
public class CCHttpResponseCallback {

    /**
     * 请求完全成功之后调用此方法（钩子方法）
     * @param info {@link CCHttpResponseInfo}
     * @param bts
     */
	public void onSuccess(final CCHttpResponseInfo info, final byte[] bts){
		//do nothing 添加具体实现
	}

    /**
     * 请求失败之后调用此方法（钩子方法）
     * @param e     {@link CCHttpException}
     * @param bts
     */
    public void onFail(final CCHttpException e, final byte[] bts) {
        //do nothing 添加具体实现
    }
	
	/**
	 * 请求正在进行，需要了解当前获取数据进度调用此方法（钩子方法）
	 * @param totalLength
	 * @param currentLength
	 */
    public void onProgress(final long totalLength, final int currentLength){
		//do nothing 添加具体实现
	}
}

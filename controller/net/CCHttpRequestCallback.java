package cc.sdkutil.controller.net;

import cc.sdkutil.model.net.CCHttpException;

/**
 * create by wangcong on 14-12-22. <br>
 * 请求反馈，支持请求上传进度及失败错误及原因检测. <br>
 */
public class CCHttpRequestCallback {

    /**
     * 请求失败，需要了解发送request请求时调用此(钩子方法)
     * @param e {@link CCHttpException}
     */
    public void onFail(CCHttpException e) {

    }
	
	/**
	 * 请求正在进行，需要了解当前获取数据进度调用此(钩子方法)
	 * @param totalLength
	 * @param currentLength
	 */
    public void onProgress(final long totalLength, final int currentLength){
		//do nothing 
	}
	
}

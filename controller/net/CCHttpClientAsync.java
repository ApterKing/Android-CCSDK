package cc.sdkutil.controller.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import cc.sdkutil.controller.core.CCSDKUtil;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.net.CCHttpException;
import cc.sdkutil.model.net.CCHttpExceptionType;
import cc.sdkutil.model.net.CCHttpResponseInfo;

/**
 *  Created by wangcong on 14-12-22. <br>
 *  异步httpclient实现. <br>
 *  @see {@link CCHttpClient}
 */
@CCDebug
public class CCHttpClientAsync {
	
	//用户构造 CCHttpRequest
	private CCHttpClient mClient;

    //用于构造 CCHttpResponse
	private CCHttpRequest mRequest;
	private CCHttpRequestCallback requestCallback;

    //用于响应请求
	private CCHttpResponse mResponse;
	private CCHttpResponseCallback responseCallback;

	//异步请求
	private FutureTask<Void> mFutureTask;
	private final ExecutorService mService;

    volatile boolean isCanceled = false;

	public CCHttpClientAsync() {
		mClient = new CCHttpClient();
		mService = Executors.newSingleThreadExecutor();
	}

	/**
	 * 设置User-Agent
	 * @param userAgent
	 */
	public CCHttpClientAsync setUserAgent(String userAgent) {
		mClient.setUserAgent(userAgent);
		return this;
	}

	/**
	 * 设置Host
	 * @param host
	 */
	public CCHttpClientAsync setHost(String host) {
		mClient.setHost(host);
		return this;
	}

	/**
	 * 设置端口号
	 * @param port
	 */
	public CCHttpClientAsync setPort(short port) {
		mClient.setPort(port);
		return this;
	}

	/**
	 * 设置连接时长
	 * @param connectionTimeOut
	 */
	public CCHttpClientAsync setConnectionTimeOut(int connectionTimeOut) {
		mClient.setConnectionTimeOut(connectionTimeOut);
		return this;
	}

	/**
	 * 设置连读取数据时长
	 * @param soTimeOut
	 */
	public CCHttpClientAsync setSoTimeOut(int soTimeOut) {
		mClient.setSoTimeOut(soTimeOut);
		return this;
	}

	/**
	 * 格式如
	 * <br>从某个位置开始到结尾: bytes=1024-
	 * <br>从某个位置到某个位置: bytes=1024-2048
	 * <br>同时指定几个range: bytes=512-1024,2048-4096
	 * @param range
	 */
	public CCHttpClientAsync setRange(String range) {
		mClient.setRange(range);
		return this;
	}

	/**
	 * 设置数据响应编码 gzip,deflate
	 * @param encoding
	 */
	public CCHttpClientAsync setContentEncoding(String encoding) {
		mClient.setContentEncoding(encoding);
		return this;
	}

	/**
	 * 设置请求头
	 * @param name  头名称
	 * @param value 头域值
	 */
	public CCHttpClientAsync setHeader(String name, String value) {
		mClient.setHeader(name, value);
		return this;
	}

	/**
	 * 取消请求
	 */
	public void cancel() {
        if (isCanceled) return;
        isCanceled = true;
		//调用取消请求方法
		if (mRequest != null) mRequest.cancel();
		if (mResponse != null) mResponse.cancel();

		//取消正在进行的任务
		if (mFutureTask != null) mFutureTask.cancel(true);
		mService.shutdownNow();
	}

    /**
     * post请求方式（无需知道上传进度)
     * @param uri
     * @param params
     * @param responseCallback
     */
	public void post(String uri, CCMultiParam params, final CCHttpResponseCallback responseCallback) {
		mRequest = mClient.post(uri, params);
		doResponse(null, responseCallback);
	}

    /**
     * post请求方式（需知道上传进度)
     * @param uri
     * @param params
     * @param requestCallback
     * @param responseCallback
     */
	public void post(String uri, CCMultiParam params, final CCHttpRequestCallback requestCallback, final CCHttpResponseCallback responseCallback) {
		mRequest = mClient.post(uri, params);
		doResponse(requestCallback, responseCallback);
	}

    /**
     * put请求方式（无需知道上传进度)
     * @param uri
     * @param params
     * @param responseCallback
     */
	public void put(String uri, CCMultiParam params, final CCHttpResponseCallback responseCallback) {
		mRequest = mClient.put(uri, params);
		doResponse(null, responseCallback);
	}

    /**
     * put请求方式（需知道上传进度)
     * @param uri
     * @param params
     * @param requestCallback
     * @param responseCallback
     */
	public void put(String uri, CCMultiParam params, final CCHttpRequestCallback requestCallback, final CCHttpResponseCallback responseCallback) {
		mRequest = mClient.put(uri, params);
		doResponse(requestCallback, responseCallback);
	}

    /**
     * delete请求方式（无需知道上传进度)
     * @param uri
     * @param params
     * @param responseCallback
     */
	public void delete(String uri, CCMultiParam params, CCHttpResponseCallback responseCallback) {
		mRequest = mClient.delete(uri, params);
		doResponse(null, responseCallback);
	}

    /**
     * delete请求方式（需知道上传进度)
     * @param uri
     * @param params
     * @param requestCallback
     * @param responseCallback
     */
	public void delete(String uri, CCMultiParam params, final CCHttpRequestCallback requestCallback, CCHttpResponseCallback responseCallback) {
		mRequest = mClient.delete(uri, params);
		doResponse(requestCallback, responseCallback);
	}

    /**
     * get请求方式
     * @param uri
     * @param params
     * @param responseCallback
     */
	public void get(String uri, Map<String, String> params, CCHttpResponseCallback responseCallback) {
		mRequest = mClient.get(uri, params);
		doResponse(null, responseCallback);
	}

    /**
     * get请求方式
     * @param uri
     * @param names
     * @param values
     * @param responseCallback
     */
	public void get(String uri, String[] names, String[] values, CCHttpResponseCallback responseCallback) {
		int length = names.length > values.length ? values.length : names.length;
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < length; i++) {
			map.put(names[i], values[i]);
		}
		get(uri, map, responseCallback);
	}

    /**
     * head请求方式
     * @param uri
     * @param params
     * @param responseCallback
     */
	public void head(String uri, Map<String, String> params, CCHttpResponseCallback responseCallback) {
		mRequest = mClient.head(uri, params);
		doResponse(null, responseCallback);
	}

    /**
     * head请求方式
     * @param uri
     * @param names
     * @param values
     * @param responseCallback
     */
	public void head(String uri, String[] names, String[] values, CCHttpResponseCallback responseCallback) {
		int length = names.length > values.length ? values.length : names.length;
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < length; i++) {
			map.put(names[i], values[i]);
		}
		head(uri, map, responseCallback);
	}

    /**
     * 处理请求连接后的数据
     * @param requestCallback
     * @param responseCallback
     */
	private void doResponse(final CCHttpRequestCallback requestCallback, final CCHttpResponseCallback responseCallback) {
		this.requestCallback = requestCallback;
		this.responseCallback = responseCallback;

		mFutureTask = new FutureTask<Void>(new Callable<Void>() {
			@Override
			public Void call() {
                // TODO Auto-generated method stub
                //执行之前检测网络连接状态
                if (!CCSDKUtil.isConnectedToNet()) {
                    CCHttpException exception = new CCHttpException();
                    exception.setType(CCHttpExceptionType.NETERROR_EXCEPTION);
                    exception.setStatusMessage(exception.getType().toString());
                    postAsync(ReqRespType.Req_fail, exception);
                    postAsync(ReqRespType.Resp_fail, exception, exception.getType().toString().getBytes());
                    return null;
                }

                //连接网络
                mResponse = mRequest.execute(new CCHttpRequestCallback() {

                    @Override
                    public void onFail(CCHttpException e) {
                        postAsync(ReqRespType.Req_fail, e);
                    }

                    @Override
                    public void onProgress(final long totalLength,
                                           final int currentLength) {
                        postAsync(ReqRespType.Req_progress, Long.valueOf(totalLength),
                                Integer.valueOf(currentLength));
                    }

                });

                //读取数据
                mResponse.read(new CCHttpResponseCallback() {

                    @Override
                    public void onSuccess(final CCHttpResponseInfo info, final byte[] bts) {
                        postAsync(ReqRespType.Resp_success, info, bts);
                    }

                    @Override
                    public void onFail(CCHttpException e, byte[] bts) {
                        postAsync(ReqRespType.Resp_fail, e, bts);
                    }

                    @Override
                    public void onProgress(final long totalLength, final int currentLength) {
                        postAsync(ReqRespType.Resp_progress, Long.valueOf(totalLength),
                                Integer.valueOf(currentLength));
                    }

                });
                return null;
            }
		}) {
			@Override
			protected void done() {
				// TODO Auto-generated method stub
				super.done();
				if (isCancelled()) {   //执行完成后，查看是否为用户做了取消操作
                    CCHttpException exception = new CCHttpException();
                    exception.setType(CCHttpExceptionType.INTRRUPT_EXCEPTION);
                    exception.setStatusMessage(exception.getType().toString());
					postAsync(ReqRespType.Req_fail, exception);
					postAsync(ReqRespType.Resp_fail, exception, exception.getMessage().getBytes());
				}
			}
		};
		if (!mService.isShutdown()) mService.submit(mFutureTask);
	}
	
	private void postAsync(final ReqRespType type, final Object... objs) {
		CCSDKUtil.getHandler().post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                switch (type) {
                    case Req_fail:
                        if (requestCallback != null)
                            requestCallback.onFail((CCHttpException) objs[0]);
                        break;
                    case Req_progress:
                        if (requestCallback != null)
                            requestCallback.onProgress(Long.parseLong(objs[0].toString()),
                                    Integer.parseInt(objs[1].toString()));
                        ;
                        break;
                    case Resp_success:
                        if (responseCallback != null)
                            responseCallback.onSuccess((CCHttpResponseInfo) objs[0], (byte[]) objs[1]);
                        break;
                    case Resp_fail:
                        if (responseCallback != null)
                            responseCallback.onFail((CCHttpException) objs[0], (byte[]) objs[1]);
                        break;
                    case Resp_progress:
                        if (responseCallback != null)
                            responseCallback.onProgress(Long.parseLong(objs[0].toString()),
                                    Integer.parseInt(objs[1].toString()));
                        ;
                        break;
                    default:
                        break;
                }
            }
        });
	}
	
	/**
	 * Created by wangcong on 14-12-22. <br>
	 * 请求响应反馈类型. <br>
	 */
	private enum ReqRespType {
		Req_fail,
		Req_progress,
		
		Resp_success,
        Resp_fail,
        Resp_progress;
	}
	
}

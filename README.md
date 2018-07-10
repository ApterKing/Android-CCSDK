#Android-CCSdk  [点击查看使用示例](https://git.oschina.net/wangcccong/Resume)
提供: 包含图片处理、图片加载、缓存处理（内存、外存）、HTTP访问请求、资源View等的加载及其他json数据解析等常用工具类

**1、图片处理**：详情查看cc.sdkutil.controller.image包下的CCBitmapHelper.java,支持比例压缩及质量压缩

**2、图片加载**：CCImageLoader.java 多图片加载工具，提供设置是否缓存到内存（默认false),是否缓存到磁盘（支持默认路径与自定义路径）,支持图片压缩比例（默认不压缩）,支持设置同时加载图片数量（默认10）,支持设置图片加载反馈; 支持取消所有任务（如果取消了所任务需要重新使用ImageLoader需rest),支持取消某个特定的正在加载任务.

使用方式:

    CCImageLoader mImageLoader = new CCImageLoader.Builder().needCacheInMemory().needCacheInDisk().outSize(128,                 128).callback(CCImageLoaderCallback).build();
    mImageLoader.loadNetImage(imgUrl, objs /*此为你想传递的附加信息，成功后返回*/);
    mImageLoader.loadResourceImage(res /* Resources */, resId, obj /*此为你想传递的附加信息，成功后返回*/);
    mImageLoader.loadLocalImage(imgPath, obj  /*此为你想传递的附加信息，成功后返回*/);
    private CCImageLoaderCallback mImageListener = new CCImageLoaderCallback() {

	/**
	 * @param bitmap  图片数据
	 * @param objs 用户传入的需要重新返回的对象
	 */
	public void onSuccess(Bitmap bitmap, Object... objs) {
	}
	
	/**
	 * @param reason  加载失败原因
	 */
	public void onFail(final String reason) {
		
	}

    };

**3、缓存处理**：详情查看cc.sdkutil.controller.cache包下的CCCacheManager.java,实现内存与磁盘LRU缓存算法，内存采用Reference检测对象是否被回收，支持分别设置缓存大小，缓存和获取数据，支持异步清空缓存并相应回调.

        // 缓存到内存
        CCCacheManager.newInstance().cacheByteInMemory(memoryPath, byte[]);
        
        // 缓存到磁盘
        CCCacheManager.newInstance().cacheByteInDisk(diskPath, byte[]);
        
        // 清空缓存
        CCCacheManager.newInstance().clearCache(fileDir, new CCDiskClearCallback() {
            @Override
            public void onCompleted(boolean success) {
                
            }
        });

**4、HTTP请求**：HTTP访问客户端，初始化客户端发起http请求; 编码方式、cookie、userAgent等, 支持（POST/GET/PUT/HEAD/DELETE）方式访问服务器端，默认支持gzip， deflate压缩；
支持错误访问出错：
    NETERROR_EXCEPTION("网络未连接"),
    READ_EXCEPTION("获取数据出错"),
    HTTPS_EXCEPTION("https 请求错误"),
    TIMEOUT_EXCEPTION("连接超时"),
    ERROR_EXCEPTION(">=400 访问服务器出错"),
    INTRRUPT_EXCEPTION("用户取消了请求操作");

get请求实例如下：

    private void getRequest() {
        final Map<String, String> param = new HashMap<String, String>() {
            {
                put("userId", "USERID");
            }
        };
        new CCHttpClientAsync().get(GET_URL, param, new CCHttpResponseCallback() {
            @Override
            public void onSuccess(CCHttpResponseInfo info, byte[] bts) {
                super.onSuccess(info, bts);
            }

            @Override
            public void onFail(CCHttpException e, byte[] bts) {
                super.onFail(e, bts);
                switch (e.getType()) {
                    case NETERROR_EXCEPTION:
                        // 网络连接错误
                        break;
                    case TIMEOUT_EXCEPTION:
                        // 连接服务器超时
                        break;
                    case INTRRUPT_EXCEPTION:
                        // 用户取消了操作
                        break;
                    case ERROR_EXCEPTION:
                        // 访问服务器出错
                        break;
                    default:
                        break;
                }
            }
        });
    }

post请求示例如下：

    private void postRequest() {
        CCMultiParam param = new CCMultiParam();
        param.put("userId", "USERID");
        param.put("other", "OTHER");
        new CCHttpClientAsync().get(POST_URL, param, new CCHttpResponseCallback() {
            @Override
            public void onSuccess(CCHttpResponseInfo info, byte[] bts) {
                super.onSuccess(info, bts);
            }

            @Override
            public void onFail(CCHttpException e, byte[] bts) {
                super.onFail(e, bts);
                switch (e.getType()) {
                    case NETERROR_EXCEPTION:
                        // 网络连接错误
                        break;
                    case TIMEOUT_EXCEPTION:
                        // 连接服务器超时
                        break;
                    case INTRRUPT_EXCEPTION:
                        // 用户取消了操作
                        break;
                    case ERROR_EXCEPTION:
                        // 访问服务器出错
                        break;
                    default:
                        break;
                }
            }
        });
    }

**5、View或者资源加载以及View监听**：

    Activity及Fragment中使用：

    @CCInjectRes(R.id.login_name_edit)
    EditText medtName;
    
    @CCInjectEvent(value = {R.id.login_radiogroup}, clazz = RadioGroup.OnCheckedChangeListener.class)
    void onCheckedChanged(RadioGroup group, int checkedId) {
        
    }

    @CCInjectEvent(value = {R.id.login_loginBtn, R.id.login_registerBtn}, clazz = View.OnClickListener.class)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_loginBtn:
                
                break;
            default:
                break;
        }
    }
    
    Adapter中使用：
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_fencelist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder {
        @CCInjectRes(R.id.fencelist_item_name)
        TextView mTxtName;
        @CCInjectRes(R.id.fencelist_item_businessRule)
        TextView mTxtRule;
        @CCInjectRes(R.id.fencelist_item_latitude)
        TextView mTxtLatituxde;
        @CCInjectRes(R.id.fencelist_item_longitude)
        TextView mTxtLongitude;
        @CCInjectRes(R.id.fencelist_item_radius)
        TextView mTxtRadius;

        public ViewHolder(View itemView) {
            CCInjectUtil.inject(this, itemView);
        }
    }
    **注意：如果是在Activity中需要调用CCInjectUtil.inject(this) 、 fragment或者其他类中采用：
    CCInjectUtil.inject(this, view) 传入一个view即可，这种和适合于Fragment在onCreatedView 及Adapter中使用**

**6、JSON数据解析、数据加密及常用工具方法使用**：详情查看cc.sdkutil.controller.util

**7、重新设计的异步访问cc.sdkutil.controller.core包下的CCAsyncHandler，在cc.sdkutil.controller.cache包下的内存及磁盘缓存中有详细使用方法**

**8、参考iOS开发重新设计的观察者模式cc.sdkutil.controller.core下的CCAbsObservable、CCObservable、CCObserver**

    // 首先在需要观察这类中实现CCObserver接口,可以是Fragment,Activity或者其他任何类中
        // 以Activity为例
        // 在onCreate中注册观察着
        CCObservable.newInstance().registerObserver("category", this);
        
        // onDestroy注销观察者
        CCObservable.newInstance().unRegisterObserver("category", this);
        
        // 其余任何地方想告知观察者做出相应
        CCObservable.newInstance().notifyObserver("category", objs...);
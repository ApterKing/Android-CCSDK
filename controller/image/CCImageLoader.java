package cc.sdkutil.controller.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.ArrayMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import cc.sdkutil.controller.cache.CCCacheManager;
import cc.sdkutil.controller.core.CCSDKUtil;
import cc.sdkutil.controller.util.CCEncryptUtil;
import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.image.CCImageInfoWrapper;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 14-12-18.
 * 多图片加载工具，提供设置是否缓存到内存（默认false),是否缓存到磁盘（支持默认路径与自定义路径）,支持
 * 图片压缩比例（默认不压缩）,支持设置同时加载图片数量（默认10）,支持设置图片加载反馈
 * 支持取消所有任务（如果取消了所任务需要重新使用ImageLoader需rest),支持取消某个特定的正在加载任务. <br>
 * @see CCImageLoaderCallback
 */
@CCDebug
public class CCImageLoader {

	/**
	 * Create on 14-12-19. <br>
	 * 导演者类
	 */
	private static class Director {
		Builder mBuilder;
		private Director(Builder builder) {
			this.mBuilder = builder;
		}

		CCImageLoader construct() {
			return new CCImageLoader(mBuilder);
		}
	}

	/**
	 * @author wangcong
	 * create on 14-12-19. <br>
	 * 建造者类
	 */
	public static class Builder {
        boolean needCacheInMemory = false;         //是否需要缓存到内存
        boolean needCacheInDisk = false;           //是否需要缓存到磁盘
        String cacheDir;                           //如果需要缓存到磁盘需要设置默认路径
        int outWidth = 0;                          //图片输出宽度用于判定是否压缩图片(<=0 表示不压缩)
        int outHeight = 0;                         //图片输出高度用于判定是否压缩图片(<=0 表示不压缩)

        int maxLoadCount = 10;                     //同时加载图片的最大任务数，默认为10
        CCImageLoaderCallback callback;            //图片加载响应

        private ExecutorService mLoadService;    //图片加载线程池
        /** 用于控制同时加载图片的最大数量,如果图片加载数量大于当前设置数量，则会自动终止最早开始加载的任务  */
        private LinkedHashMap<String, FutureTask<CCImageInfoWrapper>> mFutureTaskMap;
        private ThreadFactory mFactory;

		public Builder() {
			maxLoadCount(10);
		}

		/**
		 * 设置需要缓存图片到内存
		 */
		public Builder needCacheInMemory() {
			needCacheInMemory = true;
			return this;
		}

		/**
		 * 设置是否需要缓存到磁盘，使用此方法会将磁盘缓存到默认路径中
		 */
		public Builder needCacheInDisk() {
			needCacheInDisk = true;
			cacheDir = CCCacheManager.getImageCacheDir(CCSDKUtil.getContext());
			return this;
		}

		/**
		 * 设置是否需要缓存到磁盘，使用此方法会将磁盘缓存到指定路径中
		 * @param dirPath
		 */
		public Builder needCacheInDisk(String dirPath) {
			cacheDir = dirPath;
			File file = new File(dirPath);
			if (!file.exists()) file.mkdirs();
			return this;
		}

		/**
		 * 设置图片压缩输出的宽高
		 * @param width
		 * @param height
		 * @return
		 */
		public Builder outSize(int width, int height) {
			this.outWidth = width;
			this.outHeight = height;
			return this;
		}

		/**
		 * 设置最大加载图片的数量
		 * @param count
		 * @return
		 */
		public Builder maxLoadCount(int count) {
            this.maxLoadCount = count;
            this.mFutureTaskMap = new LinkedHashMap<String, FutureTask<CCImageInfoWrapper>>(maxLoadCount, .75f, true) {
                private static final long serialVersionUID = 1L;
                @Override
                protected boolean removeEldestEntry(
                        Entry<String, FutureTask<CCImageInfoWrapper>> eldest) {
                    // TODO Auto-generated method stub
                    //如果任务数量大于最大加载数，则取消最早的任务
                    if (size() > maxLoadCount)
                        eldest.getValue().cancel(true);
                    return size() > maxLoadCount;
                }
            };
            this.mFactory = new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    // TODO Auto-generated method stub
                    return new Thread(r, "ImageLoader--Thread:" + index.getAndIncrement());
                }
            };
            this.mLoadService = Executors.newCachedThreadPool(mFactory);
			return this;
		}

		/**
		 * 设置图片加载响应操作
		 * @param callback
		 * @return
		 */
		public Builder callback(CCImageLoaderCallback callback) {
			this.callback = callback;
			return this;
		}
		
		/**
		 * 构建一个Imageloader
		 * @return
		 */
		public CCImageLoader build() {
			CCImageLoader imageLoader = new Director(this).construct();
			return imageLoader;
		}
	}

	private Builder mBuilder;

    // 记录指定图片地址的加载任务，可用于取消指定加载请求
    private ArrayMap<String, FutureTask<CCImageInfoWrapper>> mArrayMap;

	private CCImageLoader(final Builder builder){
		this.mBuilder = builder;
        mArrayMap = new ArrayMap<>();
	}
	
	/**
	 * 取消数据加载
	 * @param now 如果为true 则立即取消所有任务，否则拒绝新添加任务，再等待已经提交的任务完成后取消
	 */
	public void cancelAll(boolean now) {
		if (now) mBuilder.mLoadService.shutdownNow();
		else mBuilder.mLoadService.shutdown();
	}
	
	/**
	 * 取消某个特定的正在加载任务
	 * @param imgUrl
	 */
	public void cancel(final String imgUrl) {
		FutureTask<CCImageInfoWrapper> task = mArrayMap.get(imgUrl);
        if (task != null)
            task.cancel(true);
	}
	
	/**
	 * 如果ImageLoader 调用了cacel方法，需要重置线程池才能重新添加任务
	 */
	public void reset() {
		mBuilder.mLoadService = Executors.newCachedThreadPool(mBuilder.mFactory);
	}
	
	/**
	 * 图片加载错误处理
	 * @param reason
	 */
	private void postFail(final String reason) {
        CCSDKUtil.getHandler().post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mBuilder.callback != null)
                    mBuilder.callback.onFail(reason);
            }
        });
	}
	
	/**
	 * 图片加载成功
	 * @param bitmap
	 * @param objs
	 */
	private void postSuccess(final Bitmap bitmap, final Object... objs) {
        CCSDKUtil.getHandler().post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mBuilder.callback != null)
                    mBuilder.callback.onSuccess(bitmap, objs);
            }
        });
	}

    /**
     * 加载网络图片
     * @param imgUrl
     * @param objs
     */
	public void loadNetImage(final String imgUrl, final Object... objs) {

		final String imagePath = mBuilder.cacheDir + (mBuilder.cacheDir.endsWith("/") ? "" : File.separator) + CCEncryptUtil.MD5(imgUrl);
		final CCImageInfoWrapper wrapper = new CCImageInfoWrapper();
		wrapper.setObjs(objs);
		wrapper.setImagePath(imagePath);

		final FutureTask<CCImageInfoWrapper> futureTask = new FutureTask<CCImageInfoWrapper>(
				new Callable<CCImageInfoWrapper>() {
			@Override
			public CCImageInfoWrapper call() {
				// TODO Auto-generated method stub
				wrapper.setLoadFromLocal(true);

				byte[] bts = CCCacheManager.newInstance().byteFromMemory(imagePath);
				if (bts == null) bts = CCCacheManager.newInstance().byteFromDisk(imagePath);
				Bitmap bitmap = bts == null ? null : BitmapFactory.decodeByteArray(bts, 0, bts.length);
				if (bitmap == null) {
					wrapper.setLoadFromLocal(false);
                    CCLogUtil.d(CCImageLoader.class.getAnnotation(CCDebug.class).debug(),
                            getClass(), "loadImage--url", imgUrl);
                    try {
                        URL conUrl = new URL(imgUrl);
                        HttpURLConnection connection = (HttpURLConnection) conUrl.openConnection();
                        connection.setConnectTimeout(20 * 1000);
                        connection.setReadTimeout(60 * 1000);
                        /** 首先将图片读入 , 注意此时刻不应该直接调用BitmapFactory.decodeStream ;
                         * 当图片过大时会出现OOM, 应先将数据读入，然后通过压缩得到图片对象(以上所述已在压缩中实现)
                         */
                        bitmap = CCBitmapHelper.compressBitmap(connection.getInputStream(), mBuilder.outWidth, mBuilder.outHeight);
                    } catch (IOException e) {
                        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                                getClass(), "loadImage--call", e.getMessage());
                    }
				}
				if (bitmap != null) {
                    SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
                    wrapper.setSoftRefBitmap(soft);
                    bitmap = null;
                }
				return wrapper;
			}
		}) {
			@Override
			protected void done() {
				// TODO Auto-generated method stub
				super.done();
				try {
					if (isCancelled()) {
						postFail("该任务已取消");
						return;
					}
					final CCImageInfoWrapper wrapper = get();
					if (wrapper.getSoftRefBitmap() == null) {
						postFail("加载图片失败");
						return;
					}
					postSuccess(wrapper.getSoftRefBitmap().get(), wrapper.getObjs());

					//图片从本地加载的无需再次缓存
					if (wrapper.isLoadFromLocal()) return;
					if (mBuilder.needCacheInMemory || mBuilder.needCacheInDisk) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						wrapper.getSoftRefBitmap().get().compress(CompressFormat.PNG, 100, baos);
						byte[] bts = baos.toByteArray();
                        baos.close();
						if (mBuilder.needCacheInMemory) CCCacheManager.newInstance().cacheByteInMemory(wrapper.getImagePath(), bts);
						if (mBuilder.needCacheInDisk) CCCacheManager.newInstance().cacheByteInDisk(wrapper.getImagePath(), bts);
					}
				} catch (InterruptedException | ExecutionException | IOException e) {
                    CCLogUtil.d(CCImageLoader.class.getAnnotation(CCDebug.class).debug(),
                            CCImageLoader.class, "loadImage--done", e.getMessage());
				}
			}
		};
		if (!mBuilder.mLoadService.isShutdown()) {
            mBuilder.mLoadService.execute(futureTask);
            mBuilder.mFutureTaskMap.put(imgUrl, futureTask);
            mArrayMap.put(imgUrl, futureTask);
		}
	}

    /**
     * 加载资源图片
     * @param res
     * @param resId
     * @param objs
     */
    public void loadResourceImage(final Resources res, final int resId, final Object... objs) {

        final CCImageInfoWrapper wrapper = new CCImageInfoWrapper();
        wrapper.setObjs(objs);

        final FutureTask<CCImageInfoWrapper> futureTask = new FutureTask<CCImageInfoWrapper>(
                new Callable<CCImageInfoWrapper>() {
                    @Override
                    public CCImageInfoWrapper call() {
                        // TODO Auto-generated method stub
                        wrapper.setLoadFromLocal(true);

                        Bitmap bitmap = CCBitmapHelper.compressBitmap(res, resId, mBuilder.outWidth, mBuilder.outHeight);
                        if (bitmap != null) {
                            SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
                            wrapper.setSoftRefBitmap(soft);
                            bitmap = null;
                        }
                        return wrapper;
                    }
                }) {
            @Override
            protected void done() {
                // TODO Auto-generated method stub
                super.done();
                try {
                    if (isCancelled()) {
                        postFail("该任务已取消");
                        return;
                    }
                    final CCImageInfoWrapper wrapper = get();
                    if (wrapper.getSoftRefBitmap() == null) {
                        postFail("加载图片失败");
                        return;
                    }
                    postSuccess(wrapper.getSoftRefBitmap().get(), wrapper.getObjs());
                } catch (InterruptedException | ExecutionException e) {
                    CCLogUtil.d(CCImageLoader.class.getAnnotation(CCDebug.class).debug(),
                            CCImageLoader.class, "loadImage--done", e.getMessage());
                }
            }
        };
        if (!mBuilder.mLoadService.isShutdown()) {
            mBuilder.mLoadService.execute(futureTask);
        }
    }

    /**
     * 从本地加载图片
     * @param imgPath
     * @param objs
     */
    public void loadLocalImage(final String imgPath, final Object... objs) {

        final CCImageInfoWrapper wrapper = new CCImageInfoWrapper();
        wrapper.setImagePath(imgPath);
        wrapper.setObjs(objs);

        final FutureTask<CCImageInfoWrapper> futureTask = new FutureTask<CCImageInfoWrapper>(
                new Callable<CCImageInfoWrapper>() {
                    @Override
                    public CCImageInfoWrapper call() {
                        // TODO Auto-generated method stub
                        wrapper.setLoadFromLocal(true);

                        Bitmap bitmap = CCBitmapHelper.compressBitmap(imgPath, mBuilder.outWidth, mBuilder.outHeight);
                        if (bitmap != null) {
                            SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
                            wrapper.setSoftRefBitmap(soft);
                            bitmap = null;
                        }
                        return wrapper;
                    }
                }) {
            @Override
            protected void done() {
                // TODO Auto-generated method stub
                super.done();
                try {
                    if (isCancelled()) {
                        postFail("该任务已取消");
                        return;
                    }
                    final CCImageInfoWrapper wrapper = get();
                    if (wrapper.getSoftRefBitmap() == null) {
                        postFail("加载图片失败");
                        return;
                    }
                    postSuccess(wrapper.getSoftRefBitmap().get(), wrapper.getObjs());
                } catch (InterruptedException | ExecutionException e) {
                    CCLogUtil.d(CCImageLoader.class.getAnnotation(CCDebug.class).debug(),
                            CCImageLoader.class, "loadImage--done", e.getMessage());
                }
            }
        };
        if (!mBuilder.mLoadService.isShutdown()) {
            mBuilder.mLoadService.execute(futureTask);
        }
    }

}
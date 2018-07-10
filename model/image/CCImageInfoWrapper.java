package cc.sdkutil.model.image;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;

/**
 * Created by wangcong on 15-1-6.
 * 用于多图片加载过程中对加载相关信息的封装. <br>
 */
public class CCImageInfoWrapper {

    private String imagePath;                        //保存到本地的图片路径
    private SoftReference<Bitmap> softRefBitmap;     //图片信息
    private boolean isLoadFromLocal;                 //是否从本地加载
    private Object[] objs;                           //加载开始时附带的参数，将原封不动的反馈给用户

    public String getImagePath() {
        return imagePath;
    }

    public SoftReference<Bitmap> getSoftRefBitmap() {
        return softRefBitmap;
    }

    public boolean isLoadFromLocal() {
        return isLoadFromLocal;
    }

    public Object[] getObjs() {
        return objs;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setSoftRefBitmap(SoftReference<Bitmap> softRefBitmap) {
        this.softRefBitmap = softRefBitmap;
    }

    public void setLoadFromLocal(boolean isLoadFromLocal) {
        this.isLoadFromLocal = isLoadFromLocal;
    }

    public void setObjs(Object[] objs) {
        this.objs = objs;
    }
}

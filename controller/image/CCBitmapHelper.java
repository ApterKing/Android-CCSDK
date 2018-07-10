package cc.sdkutil.controller.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.controller.util.CCSdcardUtil;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 14-12-18.
 * 图片处理工具，用于获取图片方向，旋转图片，压缩图片，可以压缩 byte[] , Resource 资源图片, 数据流;
 * 实现比例压缩与质量压缩. <br>
 */
@CCDebug
public class CCBitmapHelper {

	/**
	 * 获取到图片的方向
	 * @param path   图片路径
	 * @return
	 */
	public final static int getDegress(String path) {
		int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
	}
	
	/**
	 * 旋转图片
	 * @param bitmap  图片
	 * @param degress  旋转角度
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress); 
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }
	
	/**
	 * 计算需要缩放的SampleSize
	 * @param options  
	 * @param rqsW
	 * @param rqsH
	 * @return
	 */
	public final static int caculateInSampleSize(Options options, int rqsW, int rqsH) {
		final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    if (rqsW == 0 || rqsH == 0) return 1;
	    if (height > rqsH || width > rqsW) {
	    	final int heightRatio = Math.round((float) height/ (float) rqsH);
	    	final int widthRatio = Math.round((float) width / (float) rqsW);
	    	inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}

    /**
     * 压缩制定路径的图片，并得到图片对象
     * @param path
     * @param rqsW
     * @param rqsH
     * @return
     */
	public final static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
	    options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}

    /**
     *
     * @param descriptor
     * @param resW
     * @param resH
     * @return
     */
    public final static Bitmap compressBitmap(FileDescriptor descriptor, int resW, int resH) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(descriptor, null, options);
        options.inSampleSize = caculateInSampleSize(options, resW, resH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(descriptor, null, options);
    }
	
	/**
	 * 压缩制定路径图片，并将其保存在缓存目录中，通过isDelSrc判定是否删除源文件，并获取到缓存后的图片路径
	 * @param context
	 * @param srcPath
	 * @param rqsW
	 * @param rqsH
	 * @param isDelSrc
	 * @return
	 */
	public final static String compressBitmap(Context context, String srcPath, int rqsW, int rqsH, boolean isDelSrc) {
		Bitmap bitmap = compressBitmap(srcPath, rqsW, rqsH);
		File srcFile = new File(srcPath);
		String desPath = CCSdcardUtil.getCacheDir(context) + srcFile.getName();
		int degree = getDegress(srcPath);
		try {
			if (degree != 0) bitmap = rotateBitmap(bitmap, degree);
			File file = new File(desPath);
			FileOutputStream  fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 70, fos);
			fos.close();
			if (isDelSrc) srcFile.deleteOnExit();
		} catch (Exception e) {
            CCLogUtil.d(CCBitmapHelper.class.getAnnotation(CCDebug.class).debug(),
                    CCBitmapHelper.class, "compressBitmap", e.getMessage());
		}
		return desPath;
	}

    /**
     * 压缩某个输入流中的图片，可以解决网络输入流压缩问题，并得到图片对象
     * @param is
     * @param reqsW
     * @param reqsH
     * @return
     */
	public final static Bitmap compressBitmap(InputStream is, int reqsW, int reqsH) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ReadableByteChannel channel = Channels.newChannel(is);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (channel.read(buffer) != -1) {
				buffer.flip();
				while (buffer.hasRemaining()) baos.write(buffer.get());
				buffer.clear();
			}
			byte[] bts = baos.toByteArray();
			Bitmap bitmap = compressBitmap(bts, reqsW, reqsH);
			is.close();
			channel.close();
			baos.close();
			return bitmap;
		} catch (Exception e) {
            CCLogUtil.d(CCBitmapHelper.class.getAnnotation(CCDebug.class).debug(),
                    CCBitmapHelper.class, "compressBitmap-is-reqsw-reqsh", e.getMessage());
			return null;
		}
	}
	
	/**
	 * 压缩制定byte[]图片，并得到压缩后的图像
	 * @param bts
	 * @param reqsW
	 * @param reqsH
	 * @return
	 */
	public final static Bitmap compressBitmap(byte[] bts, int reqsW, int reqsH) {
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
		options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
	}
	
	/**
	 * 压缩已存在的图片对象，并返回压缩后的图片
	 * @param bitmap
	 * @param reqsW
	 * @param reqsH
	 * @return
	 */
	public final static Bitmap compressBitmap(Bitmap bitmap, int reqsW, int reqsH) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, baos);
			byte[] bts = baos.toByteArray();
			Bitmap res = compressBitmap(bts, reqsW, reqsH);
			baos.close();
			return res;
		} catch (IOException e) {
            CCLogUtil.d(CCBitmapHelper.class.getAnnotation(CCDebug.class).debug(),
                    CCBitmapHelper.class, "compressBitmap-bitmap-reqsw-reqsh", e.getMessage());
			return bitmap;
		}
	}
	
	/**
	 * 压缩资源图片，并返回图片对象
	 * @param res {@link Resources}
	 * @param resID
	 * @param reqsW
	 * @param reqsH
	 * @return
	 */
	public final static Bitmap compressBitmap(Resources res, int resID, int reqsW, int reqsH) {
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resID, options);
		options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resID, options);
	}


    /**
     * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
     * <br> 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
     * @param bitmap
     * @param maxBytes
     * @return
     */
	public final static Bitmap compressBitmap(Bitmap bitmap, long maxBytes) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, baos);
			int options = 90;
			while (baos.toByteArray().length > maxBytes) {
				baos.reset();
				bitmap.compress(CompressFormat.PNG, options, baos);
				options -= 10;
			}
			byte[] bts = baos.toByteArray();
			Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
			baos.close();
			return bmp;
		} catch (IOException e) {
            CCLogUtil.d(CCBitmapHelper.class.getAnnotation(CCDebug.class).debug(),
                    CCBitmapHelper.class, "compressBitmap-bitmap-maxbytes", e.getMessage());
			return null;
		}
	}
	
	/**
	 * 得到制定路径图片的options
	 * @param srcPath
	 * @return Options {@link Options}
	 */
	public final static Options getBitmapOptions(String srcPath) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, options);
		return options;
	}

}

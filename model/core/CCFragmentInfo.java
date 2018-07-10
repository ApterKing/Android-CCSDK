package cc.sdkutil.model.core;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * create by wangcong on 14-12-30. <br>
 * 封装一个Fragment的信息.  <br>
 */
public class CCFragmentInfo implements Parcelable {

	public final static String INFO = "FragmentInfo";

	private Class<?> clazz;         //Fragment的Class
	private Bundle bundle;          //传递过来的参数

	public CCFragmentInfo(Class<?> clazz, Bundle bundle) {
		this.clazz = clazz;
		this.bundle = bundle;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public final static Creator<CCFragmentInfo> CREATOR = new Creator<CCFragmentInfo>() {

		@Override
		public CCFragmentInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new CCFragmentInfo((Class<?>)source.readSerializable(), source.readBundle());
		}

		@Override
		public CCFragmentInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new CCFragmentInfo[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeSerializable(clazz);
		dest.writeBundle(bundle);
	}

}
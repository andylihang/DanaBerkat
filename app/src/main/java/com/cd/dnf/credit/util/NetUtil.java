package com.cd.dnf.credit.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetUtil {
	/** 未连接网络 */
	public static final int NET_NONE = 0x100001;
	/** 手机网络 */
	public static final int NET_WWAN = 0x100002;
	/** wifi网络 */
	public static final int NET_WIFI = 0x100003;
    /**
	 * 获取到当前网络连接类型 (NET_NONE/NET_WWAN/NET_WIFI)
	 * @param context
	 * @return
	 */
	public static final int netType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		State state = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (state == State.CONNECTING || state == State.CONNECTED)
			return NET_WWAN;
		state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (state == State.CONNECTING || state == State.CONNECTED)
			return NET_WIFI;
		return NET_NONE;
	}

	/**
	 * 判断当前是否连接到网络
	 * @param context
	 * @return
	 */
	public static final boolean isConnectToNet(Context context) {
		return netType(context) != NET_NONE ? true : false;
	}
}

package com.abs192.lasquare.util;

import com.abs192.lasquare.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUpdateReceiver extends BroadcastReceiver {

	MainActivity act;

	public ConnectionUpdateReceiver(MainActivity act) {
		super();
		this.act = act;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (act != null)
			act.setConnectionUpdate(checkConnection(context));
	}

	public static boolean checkConnection(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnectedOrConnecting()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}
}
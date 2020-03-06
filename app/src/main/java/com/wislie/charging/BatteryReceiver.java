package com.wislie.charging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wislie.charging.listener.BatteryCallback;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-02-29 23:39
 * desc   :
 * version: 1.0
 */
public class BatteryReceiver extends BroadcastReceiver {

    private String TAG = "BatteryReceiver";

    private BatteryCallback mCallback;

    public BatteryReceiver(BatteryCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int current = intent.getExtras().getInt("level");// 获得当前电量
        int total = intent.getExtras().getInt("scale");// 获得总电量
        int percent = current * 100 / total;

        if (mCallback != null) {
            mCallback.getBatteryInfo(total, current, percent);
        }
        Log.i(TAG, "current=" + current + " total=" + total + " percent=" + percent);
    }
}

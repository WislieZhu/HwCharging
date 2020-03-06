package com.wislie.charging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wislie.charging.listener.BatteryCallback;
import com.wislie.charging.util.VirtualBarUtil;
import com.wislie.charging.view.HwChargingView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "HWChargingTest";

    private BatteryReceiver mReceiver;

    private TextView mProgressTv;
    private SeekBar mProgressSb;

    private HwChargingView mHwChargingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** 判断是否需要隐藏底部的虚拟按键 */
        if (VirtualBarUtil.hasNavBar(this)) {
            VirtualBarUtil.hideBottomUIMenu(this);
        }
        setContentView(R.layout.activity_main);
        mHwChargingView = findViewById(R.id.hw_charging);
        mProgressTv = findViewById(R.id.seek_tv);
        mProgressSb = findViewById(R.id.seekBar);
        mProgressSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgressTv.setText(progress + "%");
                mHwChargingView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        /** 手机当前电量变化的广播监测 */
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mReceiver = new BatteryReceiver(new BatteryCallback() {
            @Override
            public void getBatteryInfo(int total, int current, int percent) {
                mHwChargingView.setProgress(percent);

                mProgressTv.setText(percent + "%");
                mProgressSb.setProgress(percent);
            }
        });
        registerReceiver(mReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 注销手机电量变化的监测广播 */
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}

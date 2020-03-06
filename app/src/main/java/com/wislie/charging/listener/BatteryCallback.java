package com.wislie.charging.listener;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-02-09 23:46
 * desc   : 获取手机电量
 * version: 1.0
 */
public interface BatteryCallback {

    /**
     * 获取手机电量信息
     * @param total 总电量
     * @param current 当前电量
     * @param percent 电量百分比
     */
    void getBatteryInfo(int total, int current, int percent);
}

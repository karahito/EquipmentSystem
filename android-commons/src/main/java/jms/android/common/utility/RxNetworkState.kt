/*
 * Copyright (C) 2017 Japan Micro System
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 */

package jms.android.common.utility

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import io.reactivex.subjects.BehaviorSubject
import jms.android.common.LogUtil

/**
 *
 */
class RxNetworkState :BroadcastReceiver() {

    companion object {
        @JvmStatic
        fun newInstance(): RxNetworkState = RxNetworkState()
    }
    val subject:BehaviorSubject<Any> = BehaviorSubject.create()

    override fun onReceive(context: Context, intent: Intent?) {
        val info = getNetworkInfo(context)
        if(info != null)
            subject.onNext(info)
        else
            subject.onNext(Unit)
//            subject.onError(ClassCastException("Network has not found"))
    }

    private val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    fun registerReceiver(context: Context){
        context.registerReceiver(this,filter)
    }

    fun unregisterReceiver(context: Context){
        context.unregisterReceiver(this)
    }

    /**
     * 接続済みのネットワークの情報を取得する。
     *
     * @param context
     * @return NetworkInfo,WifiInfoまたはnull
     *         Wifi接続時のみWifiInfoを返す。
     *         ネットワークが検出できない場合はnullを返す。
     */
    @SuppressLint("MissingPermission")
    fun getNetworkInfo(context:Context):Any? {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val info = manager?.activeNetworkInfo
        return when(manager?.activeNetworkInfo?.type){
            ConnectivityManager.TYPE_WIFI ->{
                LogUtil.d("Network state is Wifi")
                val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                val wifiInfo = wifiMgr.connectionInfo
                return wifiMgr.connectionInfo
            }
            ConnectivityManager.TYPE_WIMAX -> {
                LogUtil.d("Network state is WiMax")
                return info
            }
            ConnectivityManager.TYPE_MOBILE->{
                LogUtil.d("Network state is Mobile 4G")
                return info
            }
            ConnectivityManager.TYPE_MOBILE_DUN->{
                LogUtil.d("Network state is Mobile 3G")
                return info
            }
            ConnectivityManager.TYPE_VPN ->{
                LogUtil.d("Network state is VPN")
                return info
            }
            ConnectivityManager.TYPE_BLUETOOTH ->{
                LogUtil.d("Network state is Bluetooth")
                return info
            }
            ConnectivityManager.TYPE_ETHERNET ->{
                LogUtil.d("Network state is Ethernet")
                return info
            }
            else -> {
                null
            }
        } ?: return null
    }

}
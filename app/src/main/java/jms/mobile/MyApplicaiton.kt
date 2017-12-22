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

package jms.mobile

import android.app.Application
import android.content.Context
import io.realm.Realm
import jms.android.common.utility.RestApiBuilder
import jms.mobile.Entity.DBTable.TransactionHistory
import jms.mobile.Service.EqmsService
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*

/**
 * Custom Application class
 *  基本的にアプリケーション起動/終了時の処理を記述する。
 *  applicationContextや通信用プロトコルなど複数のアクティビティで使用するものはここでSingleton生成を行う。
 *  @author D.Noguchi
 *  @Since 2.Sep,2017
 */
class MyApplication:Application() {
    companion object {
        @JvmStatic
        val baseURL = "http://192.168.1.11"
        @JvmStatic
        lateinit var appContext:Context
    }
    init {
        RestApiBuilder.build<EqmsService>(baseURL, RxJava2CallAdapterFactory.create(), JspoonConverterFactory.create())
    }

    /**
     * アプリケーション初期化処理
     * DBのインスタンスを初期化する。
     */
    override fun onCreate() {
        super.onCreate()
        appContext = this.applicationContext
        val calender = Calendar.getInstance()
        calender.add(Calendar.MONTH,-2)
        Realm.init(appContext)
        val realm = Realm.getDefaultInstance()
        val history = realm.where(TransactionHistory::class.java).lessThan("transactionDate",calender.time).findAll()
        realm.beginTransaction()
        history.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
    }
}
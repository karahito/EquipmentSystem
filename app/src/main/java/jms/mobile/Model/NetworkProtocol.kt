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

package jms.mobile.Model

import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.realm.Realm
import jms.android.common.LogUtil
import jms.android.common.utility.RestApiBuilder
import jms.mobile.Entity.DBTable.TransactionHistory
import jms.mobile.Entity.LoginResponse
import jms.mobile.Entity.TransactionResponse
import jms.mobile.Service.EqmsService
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*

/**
 * @Singleton
 * RestAPIをUIThreadとブリッジするためのプロトコル
 *
 * @author D.Noguchi
 * @since 11,Dec.2017
 */
object NetworkProtocol {
    private val client:EqmsService
    init {
        val baseURL = "http://192.168.1.11"
         RestApiBuilder.build<EqmsService>(baseURL, RxJava2CallAdapterFactory.create(), JspoonConverterFactory.create())
        client = RestApiBuilder.provider as EqmsService
    }

    internal var user:String? = null
    internal var wCode:String? = null


    fun login(wCode:String):Single<String>{
        return Single.create {
            emitter->
            loginProtocol(wCode,emitter).dispose()
        }
    }
    private fun loginProtocol(wCode: String, emitter: SingleEmitter<String>):Disposable{
        return client.login(wCode)
                .subscribe(
                        {
                            val res = parseJson<LoginResponse>(it.json?:return@subscribe emitter.onError(ClassCastException("Illegal Response")))
                            val errorCode = StringBuilder()
                            res.error?.forEach {
                                errorCode.appendln(it)
                            }
                            emitter.onSuccess(res.wName ?: return@subscribe emitter.onError(ClassCastException(errorCode.toString())))
                        },
                        {
                            emitter.onError(it)
                        })
    }

    private inline fun <reified T>parseJson(json:String):T{
        return Gson().fromJson(
                json,
                T::class.java
        )
    }


    private fun transactionProtocol(wCode:String, eCode:Array<String>,emitter: SingleEmitter<Pair<String,MutableList<TransactionHistory>>>):Disposable{

        return client.transaction(wCode,eCode)
                .subscribe(
                        {
                            LogUtil.d(it.json)
                            if (it.json.isNullOrBlank()) {
                                return@subscribe emitter.onSuccess(Pair("Transaction Success", generateTransactionHistoryOnSuccess(wCode,eCode)))
                            }
                            val error:TransactionResponse = parseJson(it.json!!)
                            return@subscribe emitter.onSuccess(Pair("Failed", generateTransactionHistoryOnFailed(wCode,eCode,error)))
                        },
                        {
                            emitter.onError(it)
                        })
    }

    private fun generateTransactionHistoryOnFailed(wCode:String, eCode: Array<String>, error:TransactionResponse):MutableList<TransactionHistory>{
        val realm = Realm.getDefaultInstance()
        var lastIndex = realm.where(TransactionHistory::class.java).findAll().lastIndex
        val date = Calendar.getInstance().time
        val history = mutableListOf<TransactionHistory>()
        eCode.forEach { code->
            try {
                val result = error.ecode?.first { it == code }
                val index = error.ecode?.indexOf(result)
                history += TransactionHistory(lastIndex,code,wCode,date,1, error.error!![index!!])
            }catch (e:NoSuchElementException){
                history.add(TransactionHistory(lastIndex,code,wCode,date,0,"-"))
            }
            lastIndex += 1
        }
        realm.close()
        return history
    }

    private fun generateTransactionHistoryOnSuccess(wCode:String, eCode: Array<String>):MutableList<TransactionHistory>{
        val realm = Realm.getDefaultInstance()
        var lastIndex = realm.where(TransactionHistory::class.java).findAll().lastIndex
        val date = Calendar.getInstance().time
        val history = mutableListOf<TransactionHistory>()
        eCode.forEach {
            history.add(TransactionHistory(lastIndex,it,wCode,date,0,"-"))
            lastIndex += 1
        }
        realm.close()
        return history
    }
    fun transaction(wCode: String,eCode:Array<String>):Single<Pair<String,MutableList<TransactionHistory>>>{
        return Single.create{emitter->
            transactionProtocol(wCode,eCode,emitter).dispose()
        }
    }
}
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

package jms.android.common

import android.util.Log



/**
 * Created by noguchi2 on 2017/11/02.
 *
 * Log出力ユーティリティクラス
 *
 * Log出力にはこれを使用すること
 */
 object LogUtil{

    private val debug = BuildConfig.DEBUG
    private val TAG = this::class.java.simpleName


    /**
     * コール元のメタ情報を取得.
     *
     * @return [className#methodName:line]:
     */
    private val metaInfo:String
        get(){
            val element = Thread.currentThread().stackTrace[4]
            return getMetaInfo(element)
        }

    /**
     * Log出力フィルタ : Verbose
     *  -Log.v(TAG,metaInfo)
     */
    fun v(){
        if(debug){
            Log.v(TAG, metaInfo)
        }
    }

    /**
     * Log出力フィルタ : Verbose
     * -Log.v(TAG,metaInfo : message)
     * @param message
     */
    fun v(message:String?){
        if(debug){
            Log.v(TAG,"$metaInfo : ${null2str(message)}")
        }
    }

    /**
     * Log出力フィルタ : Debug
     *  -Log.d(TAG,metaInfo)
     */
    fun d(){
        if(debug){
            Log.d(TAG, metaInfo)
        }
    }

    /**
     * Log出力フィルタ : Debug
     *  -Log.d(TAG,metaInfo : message)
     * @param message
     */
    fun d(message:String?){
        if (debug){
            Log.d(TAG,"$metaInfo : ${null2str(message)}")
        }
    }

    /**
     * Log出力フィルタ : Info
     *  -Log.i(TAG,metaInfo)
     */
    fun i(){
        if(debug)
            Log.i(TAG, metaInfo)
    }

    /**
     * Log出力フィルタ : Info
     *  -Log.i(TAG,metaInfo : message)
     *  @param message
     */
    fun i(message: String?){
        if(debug)
            Log.i(TAG,"$metaInfo : ${null2str(message)}")
    }

    /**
     * Log出力フィルタ : Warning
     *  -Log.w(TAG,metaInfo : message)
     *  @param message
     */
    fun w(message: String?){
        if (debug)
            Log.w(TAG,"$metaInfo : ${null2str(message)}")
    }

    /**
     * Log出力フィルタ :Warning
     *  -Log.w(TAG,metaInfo : message,e)
     *  printThrowable(e)
     *  printThrowable(e.cause)
     *
     *  @param message
     *  @param e
     */
    fun w(message: String?,e: Throwable){
        if (debug) {
            Log.w(TAG,"$metaInfo : ${null2str(message)}",e)
            printThrowable(e)
            if(e.cause != null)
                printThrowable(e.cause!!)
        }
    }
    /**
     * Log出力フィルタ : Error
     *  -Log.w(TAG,metaInfo : message)
     *  @param message
     */
    fun e(message: String?){
        if (debug)
            Log.e(TAG,"$metaInfo : ${null2str(message)}")
    }

    /**
     * Log出力フィルタ : Error
     *  -Log.w(TAG,metaInfo : message,e)
     *  printThrowable(e)
     *  printThrowable(e.cause)
     *
     *  @param message
     *  @param e
     */
    fun e(message: String?,e: Throwable){
        if (debug) {
            Log.e(TAG,"$metaInfo : ${null2str(message)}",e)
            printThrowable(e)
            if(e.cause != null)
                printThrowable(e.cause!!)
        }
    }

    /**
     * Log出力フィルタ : Erroe
     *  printThrowable(e)
     *  printThrowable(e.cause)
     *
     *  @param e
     */
    fun e(e:Throwable){
        if(debug){
            printThrowable(e)
            if(e.cause != null)
                printThrowable(e.cause!!)
        }
    }
    /**
     * 文字列:null代入変換
     * @param string
     * @return non-null:string
     *          null:(null)
     */
    private fun null2str(string: String?):String = string ?: "(null)"

    /**
     * 例外のスタックトレースをログ出力する
     *  -Log.e(tag,msg)
     *
     * @param e:Throwable
     *
     */
    private fun printThrowable(e:Throwable){
        Log.e(TAG,"${e.javaClass.name}:${e.message}")
        e.stackTrace.forEach {
            Log.e(TAG," at ${getMetaInfo(it)}")
        }
    }


    /**
     * スタックトレースから、クラス名#メソッド名:行数を取得.
     *
     * @return [className#mehodName:line]
     */
    private fun getMetaInfo(element: StackTraceElement):String{
        val fullClassName = element.className
        val simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".")+1)
        val methodName = element.methodName
        val lineNumber = element.lineNumber

        return "[$simpleClassName#$methodName:$lineNumber]"
    }
}
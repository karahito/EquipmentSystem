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

package jms.mobile.Service

import io.reactivex.Single
import jms.mobile.Entity.JsonFromHtml
import retrofit2.http.*

/**
 * RestApi
 *
 * @author D.Noguchi
 * @Since 6.Sep.2017
 */
interface EqmsService {

    /**
     * 棚卸トランザクション
     * GET
     * @param wCode 棚卸実行者番号
     * @param eCode 対象固有番号
     *
     * @return RxSingle
     *          onSuccess:JsonObject
     *                     棚卸成功→  JsonObject is null
     *                     棚卸失敗→  棚卸失敗理由。対象ECODE
     *          onError:Error
     */
    @GET("/inventories")
    fun transaction(@Query("WCODE",encoded = true) wCode: String,@Query("ECODE", encoded = true)  eCode:Array<String>):Single<JsonFromHtml>


    /**
     * ログイントランザクション
     * @param wCode 社員番号
     *
     * @return RxSingle
     *          onSuccess:JsonObject
     *                      成功→ 社員名
     *                      失敗→ 失敗理由。
     *          onError:
     */
    @GET("/inventories")
    fun login(@Query("WCODE") wCode:String): Single<JsonFromHtml>
}
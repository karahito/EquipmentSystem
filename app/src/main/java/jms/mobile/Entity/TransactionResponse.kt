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

package jms.mobile.Entity

import com.google.gson.annotations.SerializedName

/**
 * RestAPI @Get
 * 棚卸トランザクションエラーレスポンス
 * レスポンスがnullの際はトランザクションが全て正常に終了したとみなす。
 *
 * @param error エラーコード
 * @param eCode エラー発生固有番号
 *
 * @author D.Noguchi
 * @Since 8,Dec.207
 */
data class TransactionResponse(
        /** Jsonからアノテーションでシリアライズされた変数をパースする*/
        @SerializedName("ERROR")
        val error:List<String>?,
        @SerializedName("ECODE")
        val ecode:List<String>?
)
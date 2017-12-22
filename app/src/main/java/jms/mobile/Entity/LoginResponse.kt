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
 *
 * RestAPI@Get
 * ログインレスポンス
 *
 * @param wName ログイン成功時のレスポンス、社員名
 * @param error ログイン失敗時のレスポンス、エラーログ
 *
 * @author D.Noguchi
 * @since 8,Dec.2017
 */
data class LoginResponse(
        /** Jsonからアノテーションでシリアライズされた変数をパースする。 */
        @SerializedName("NAME")
        val wName:String? = null,
        @SerializedName("ERROR")
        val error:List<String>? = null
)
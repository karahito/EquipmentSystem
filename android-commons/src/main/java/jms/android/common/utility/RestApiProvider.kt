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

@file:Suppress("UNCHECKED_CAST")

package jms.android.common.utility

import retrofit2.CallAdapter
import retrofit2.Converter

/**
 *
 */
object RestApiProvider {
    val builder = RestApiBuilder.newInstance()

    inline fun <reified Interface>client(baseURL: String, callAdapter: CallAdapter.Factory, converter: Converter.Factory):Interface{
        return builder.build(baseURL,callAdapter,converter)
    }
}
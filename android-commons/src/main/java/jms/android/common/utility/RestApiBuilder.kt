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

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 */
class RestApiBuilder {

    companion object {
        @JvmStatic
        fun newInstance(): RestApiBuilder = RestApiBuilder()

        @JvmStatic var provider:Any? = null
        @JvmStatic
        inline fun <reified Interface> build(baseURL: String, callAdapter: CallAdapter.Factory, converter: Converter.Factory){
             val builder = Retrofit.Builder()
                    .client(OkHttpClient())
                    .baseUrl(baseURL)
                    .addCallAdapterFactory(callAdapter)
                    .addConverterFactory(converter)
                     .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Interface::class.java)
            provider = builder
        }
    }

    inline fun <reified Interface> build(baseURL: String, callAdapter: CallAdapter.Factory, converter: Converter.Factory):Interface{
        return Retrofit.Builder()
                .client(OkHttpClient())
                .baseUrl(baseURL)
                .addCallAdapterFactory(callAdapter)
                .addConverterFactory(converter)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Interface::class.java)
    }


}
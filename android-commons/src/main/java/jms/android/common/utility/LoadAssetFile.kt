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

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.io.IOException





/**
 *
 */
object LoadAssetFile {
    @Throws(IOException::class)
    fun assetJSONFile(context: Context, fileName:String):String{
        val manager = context.assets
        val file = manager.open(fileName)
        val formArray = ByteArray(file.available())
        file.read(formArray)
        file.close()
        return String(formArray)
    }


    @Throws(JsonParseException::class)
    inline fun <reified T> parseAssetJSONFile(context:Context,fileName: String):T{
        val json = assetJSONFile(context,fileName)
        return Gson().fromJson(json,T::class.java)
    }

}
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

import android.annotation.SuppressLint
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.exceptions.RealmException
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import jms.android.common.LogUtil

/**
 *
 */
object DbManagement {
    @SuppressLint("StaticFieldLeak")
    lateinit var realm:Realm

    fun openDb():Any{
        return try{
            realm = Realm.getDefaultInstance()
            Unit
        }catch (e:RealmException){
            LogUtil.e(e)
            e
        }
    }

    fun closeDb():Any{
        return try{
            if (realm.isClosed) {
               Unit
            }else{
                realm.close()
                Unit
            }
        }catch (e:RealmException){
            LogUtil.e(e)
            e
        }
    }

    inline fun <reified T:RealmModel> read():RealmResults<T> = realm.where(T::class.java).findAll()

    inline fun <reified T:RealmModel> write(src:T):Any{
        return try{
            realm.beginTransaction()
            realm.insert(src)
            realm.commitTransaction()
            Unit
        }catch (e:RealmException){
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }catch (e:RealmPrimaryKeyConstraintException) {
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }
    }

    inline fun <reified T:RealmModel> update(src:T):Any{
        return try{
            realm.beginTransaction()
            realm.insertOrUpdate(src)
            realm.commitTransaction()
            Unit
        }catch (e:RealmException){
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }
    }

    inline fun <reified T:RealmModel> delete(src:T):Any{
        return try{
            realm.beginTransaction()
            val query = realm.where(T::class.java).findAll()
            val index = query.indexOf(src)
            query.deleteFromRealm(index)
            realm.commitTransaction()
            Unit
        }catch (e:RealmException){
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }catch (e:RealmPrimaryKeyConstraintException) {
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }
    }

    inline fun <reified T:RealmModel> delete(src:MutableList<T>):Any{
        return try{
            realm.beginTransaction()
            val query = realm.where(T::class.java).findAll()
            src.forEach {
                query.deleteFromRealm(query.indexOf(it))
            }
            realm.commitTransaction()
            Unit
        }catch (e:RealmException){
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }catch (e:RealmPrimaryKeyConstraintException) {
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }
    }
    inline fun <reified T:RealmModel> insertOrUpdate(src:T):Any{
        return try{
            realm.beginTransaction()
            realm.insertOrUpdate(src)
            realm.commitTransaction()
            Unit
        }catch (e:RealmException){
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }
    }

    inline fun <reified T:RealmModel> insertOrUpdate(src:MutableList<T>):Any{
        return try{
            realm.beginTransaction()
            realm.insertOrUpdate(src)
            realm.commitTransaction()
            Unit
        }catch (e:RealmException){
            realm.commitTransaction()
            LogUtil.e(e)
            e
        }
    }

}
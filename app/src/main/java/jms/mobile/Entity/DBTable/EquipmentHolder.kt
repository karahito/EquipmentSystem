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

package jms.mobile.Entity.DBTable

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Realm DBTable
 * 棚卸待機中テーブル
 *
 * @param eCode         固有番号
 * @param eWorker       管理者番号(手動入力時はnull）
 * @param workerName    管理者名(手動入力時はnull）  　
 *
 * @author D.Noguchi
 * @since 2,Nov.2017
 */
@RealmClass
open class EquipmentHolder(
        @PrimaryKey
        internal var eCode:String = "",
        internal var eWorker:String? = null,
        internal var workerName:String? = null
):RealmModel
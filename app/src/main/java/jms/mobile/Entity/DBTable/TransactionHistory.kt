
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
import java.util.*

/**
 * Realm DBTable
 * 棚卸トランザクション履歴
 *
 * @param index                 主キー：インデックス
 * @param eCode                 固有番号
 * @param wCode                 実行者社員番号
 * @param transactionDate       棚卸日
 * @param connectionType        ネットワーク接続タイプ(Wifi or Mobile)
 * @param errorCode             エラーコード
 *
 * @Since 8,Dec.2017
 * @author D.Noguchi
 */
@RealmClass
open class TransactionHistory(
        @PrimaryKey
        var index:Int=0,
        var eCode:String = "",
        var wCode:String = "",
        var transactionDate:Date? = null,
        var result:Int = 0,
        var errorCode:String = ""
):RealmModel
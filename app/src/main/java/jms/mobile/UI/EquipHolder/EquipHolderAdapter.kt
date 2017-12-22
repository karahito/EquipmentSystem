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

package jms.mobile.UI.EquipHolder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import jms.mobile.Entity.DBTable.EquipmentHolder
import jms.mobile.R

/**
 * 棚卸待機中リスト管理用アダプター
 * @author D.Noguchi
 * @since 2,Nov.2017
 */
class EquipHolderAdapter(context:Context,orderCollection:OrderedRealmCollection<EquipmentHolder>):RealmBaseAdapter<EquipmentHolder>(orderCollection),ListAdapter {
    companion object {
        private val MANUAL = "-"
    }


    /** resのLayoutResourceを参照するためにLayoutInflaterを生成*/
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    /**
     * ButterKnifeでViewFiledを定義
     * ＊この時点ではBindされていないので参照値はnull
     */
    @BindView(R.id.row_holder_code) lateinit var mCode:TextView
    @BindView(R.id.row_holder_worker) lateinit var mWorker:TextView
    @BindView(R.id.row_holder_name) lateinit var mName:TextView

    @SuppressLint("InflateParams", "SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /**
         * Viewを取得する
         * convertViewがNullの場合は参照するViewを定義する
         */
        val view = convertView ?: inflater.inflate(R.layout.list_row_equipholder,null)

        /** ButterKnifeで定義したViewFieldをbindする*/
        ButterKnife.bind(this,view)

        adapterData?.get(position)?.run {
            mCode.text = eCode
            mWorker.text = eWorker ?: MANUAL
            mName.text = workerName ?: MANUAL
        }

        return view
    }
}
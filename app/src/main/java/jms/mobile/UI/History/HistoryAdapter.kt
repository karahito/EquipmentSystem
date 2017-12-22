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

package jms.mobile.UI.History

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import jms.mobile.Entity.DBTable.TransactionHistory
import jms.mobile.R
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */
class HistoryAdapter(context: Context,orderCollection:OrderedRealmCollection<TransactionHistory>):RealmBaseAdapter<TransactionHistory>(orderCollection),ListAdapter{
    companion object {
        private val MANUAL = "manual"
    }
    /** resのLayoutResourceを参照するためにLayoutInflaterを生成*/
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val resouce = context.resources

    /**
     * ButterKnifeでViewFiledを定義
     * ＊この時点ではBindされていないので参照値はnull
     */
    @BindView(R.id.row_history_date) lateinit var mDate:TextView
    @BindView(R.id.row_history_code) lateinit var mCode:TextView
    @BindView(R.id.row_history_worker) lateinit var mWorker:TextView
    @BindView(R.id.row_history_connection) lateinit var mResult:TextView
    @BindView(R.id.row_history_error) lateinit var mError:TextView

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams", "SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /**
         * Viewを取得する
         * convertViewがNullの場合は参照するViewを定義する
         */
        val view = convertView ?: inflater.inflate(R.layout.list_row_history,null)

        /** ButterKnifeで定義したViewFieldをbindする*/
        ButterKnife.bind(this,view)

        adapterData?.get(position)?.run {
            mDate.text = SimpleDateFormat("yyyy/MM/dd").format(transactionDate)
            mCode.text = eCode
            mWorker.text = wCode
            if (result == 0) mResult.text = "Success"
            else mResult.text ="Failed"
            mError.text = errorCode

        }

        when(mResult.text){
            "Success"->{
                mDate.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.success))
                    setTextColor(ContextCompat.getColor(view.context,R.color.black))
                }
                mCode.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.success))
                    setTextColor(ContextCompat.getColor(view.context,R.color.black))
                }
                mWorker.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.success))
                    setTextColor(ContextCompat.getColor(view.context,R.color.black))
                }
                mResult.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.success))
                    setTextColor(ContextCompat.getColor(view.context,R.color.black))
                }
                mError.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.success))
                    setTextColor(ContextCompat.getColor(view.context,R.color.black))
                }
            }
            else->{
                mDate.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.failed))
                    setTextColor(ContextCompat.getColor(view.context,R.color.colorText))
                }
                mCode.run {
                    setBackgroundColor(ContextCompat.getColor(view.context, R.color.failed))
                    setTextColor(ContextCompat.getColor(view.context, R.color.colorText))
                }
                mWorker.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.failed))
                    setTextColor(ContextCompat.getColor(view.context,R.color.colorText))
                }
                mResult.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.failed))
                    setTextColor(ContextCompat.getColor(view.context,R.color.colorText))
                }
                mError.run {
                    setBackgroundColor(ContextCompat.getColor(view.context,R.color.failed))
                    setTextColor(ContextCompat.getColor(view.context,R.color.colorText))
                }
            }
        }
        return view
    }
}
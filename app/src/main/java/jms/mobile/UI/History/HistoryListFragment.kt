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

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.Realm
import io.realm.Sort
import jms.mobile.Entity.DBTable.TransactionHistory
import jms.mobile.Model.DbManagement
import jms.mobile.R

/**
 *
 */
class HistoryListFragment:Fragment(){
    companion object {
        @JvmStatic fun newInstance():Fragment=HistoryListFragment()
    }

    @BindView(R.id.list_history) lateinit var mList: ListView

    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val collection = realm.where(TransactionHistory::class.java).findAllSorted("index",Sort.DESCENDING)
        val collection = DbManagement.read<TransactionHistory>().sort("index",Sort.DESCENDING)
        adapter = HistoryAdapter(this.context!!, collection)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.list_history, container, false)
        ButterKnife.bind(this,view)
        mList.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        mList.deferNotifyDataSetChanged()
    }
}
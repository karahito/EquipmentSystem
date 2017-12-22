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

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import butterknife.BindView
import butterknife.ButterKnife
import io.realm.OrderedRealmCollection
import jms.mobile.Entity.DBTable.EquipmentHolder
import jms.mobile.Model.DbManagement
import jms.mobile.R

/**
 *
 */
class EquipHolderFragment :Fragment(){
    companion object {
        @JvmStatic fun newInstance():Fragment = EquipHolderFragment()
//        @JvmStatic private val SORT = "eCode"
    }

    private val mDialogPositiveListener = DialogInterface.OnClickListener { dialogInterface, _ ->
        dialogInterface.dismiss()
        DbManagement.delete(selectedItem?:return@OnClickListener)
    }

    private val mListLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
        selectedItem = collection[position]

        val message = """
                固有番号:${selectedItem?.eCode}
                をリストから削除します。
                宜しいですか？
            """
        val dialog = AlertDialog.Builder(context!!)
                .setTitle("削除確認")
                .setMessage(message)
                .setPositiveButton("Done.",mDialogPositiveListener)
                .setNegativeButton("Cancel",null)
                .create()
                .show()

        true
    }

    @BindView(R.id.list_holder) lateinit var mList:ListView

    private lateinit var adapter: EquipHolderAdapter
    private lateinit var collection:OrderedRealmCollection<EquipmentHolder>
    private var selectedItem:EquipmentHolder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collection = DbManagement.read<EquipmentHolder>()
        adapter = EquipHolderAdapter(this.context!!, collection)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.list_equipholder, container, false)
        ButterKnife.bind(this,view)
        mList.adapter = adapter
        mList.onItemLongClickListener = mListLongClickListener
        return view
    }

    override fun onResume() {
        super.onResume()
        mList.deferNotifyDataSetChanged()
    }
}
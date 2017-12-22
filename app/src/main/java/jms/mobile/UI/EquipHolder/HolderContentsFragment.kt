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

@file:Suppress("DEPRECATION")

package jms.mobile.UI.EquipHolder

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import jms.android.common.LogUtil
import jms.mobile.Entity.DBTable.EquipmentHolder
import jms.mobile.Model.DbManagement
import jms.mobile.Model.NetworkProtocol
import jms.mobile.R


/**
 *
 */
class HolderContentsFragment:Fragment(){

    companion object {
        @JvmStatic fun newInstance():Fragment = HolderContentsFragment()
        @JvmStatic lateinit var manager:FragmentManager
        @JvmStatic val list = EquipHolderFragment.newInstance()
        @JvmStatic val client = NetworkProtocol
        @JvmStatic val INPUT_MAX_LENGTH =  5
    }

    /**
     *
     * Dialog PositiveLabel ClickListener
     * 固有番号手動入力ダイアログリスナ
     *
     * 入力Fieldの文字数が正統（５桁）なら
     * DBに入力された固有番号をストックする。
     *
     */
    private val mDialogListener = DialogInterface.OnClickListener { _, _ ->
        if (inputField.text.length == INPUT_MAX_LENGTH) {
            progress.setCancelable(false)
            progress.show()
                val callback = DbManagement.write(EquipmentHolder(inputField.text.toString()))
                if(callback is RealmPrimaryKeyConstraintException) {
                    Toast.makeText(context, "登録済み", Toast.LENGTH_SHORT).show()
                }
                progress.dismiss()
            }
        inputField.text.clear()
    }

    @BindView(R.id.fabAdd) lateinit var mAdd:FloatingActionButton
    @BindView(R.id.fabTransaction) lateinit var mTransaction:FloatingActionButton

    private lateinit var  progress:ProgressDialog
    private lateinit var inputField:EditText
    private lateinit var dialog:AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress = ProgressDialog(context)

        inputField = EditText(context)
        val maxTextLength = 5
        val lengthFilter = InputFilter.LengthFilter(maxTextLength)
        inputField.filters = arrayOf<InputFilter>(lengthFilter)
        inputField.inputType = EditorInfo.TYPE_CLASS_NUMBER

        dialog = AlertDialog.Builder(context!!)
                .setView(inputField)
                .setTitle("備品管理番号入力")
                .setMessage("管理番号（5桁）を入力してください。")
                .setPositiveButton("OK.",mDialogListener)
                .setNegativeButton("Cancel.",null)
                .create()

        manager = childFragmentManager
        manager.beginTransaction()
                .add(R.id.holder_list, list)
                .commit()

    }
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contents_holder, container, false)
        ButterKnife.bind(this,view)

        mAdd.setOnClickListener {
            dialog.show()
        }

        mTransaction.setOnClickListener {
            val wCode = client.wCode
            if (wCode != null) {
                progress.setCancelable(false)
                progress.show()
                val result = DbManagement.read<EquipmentHolder>()
                val eCode = mutableListOf<String>()
                result.forEach {
                    eCode.add(it.eCode)
                }
                client.transaction(wCode,eCode.toTypedArray())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    Toast.makeText(context,it.first,Toast.LENGTH_SHORT).show()
                                    DbManagement.insertOrUpdate(it.second)
                                    DbManagement.delete<EquipmentHolder>(result)
                                    progress.dismiss()
                                },
                                {
                                    LogUtil.e(it)
                                    progress.dismiss()
                                }
                        )

            }
        }

        return view
    }
}
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

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText


/**
 *
 */
open class InputDialogFragment: DialogFragment(){
    companion object {
        @JvmStatic fun newInstance():DialogFragment = InputDialogFragment()
        @JvmStatic private lateinit var mCallback:CallBack
        /**
         * Bundle用キーコード
         */
        @JvmStatic val KEY_TITLE = "title"
        @JvmStatic val KEY_MESSAGE ="message"
        @JvmStatic val KEY_ITEMS = "items"
        @JvmStatic val KEY_POSITIVE ="positiveLabel"
        @JvmStatic val KEY_NEGATIVE = "negativeLabel"
        @JvmStatic val KEY_CANCELABLE = "cancelable"
        @JvmStatic val KEY_ARGS = "args"
        @JvmStatic val KEY_REQUEST_CODE = "request_code"

    }

    private val requestCode:Int
        get() = if (arguments!!.containsKey(KEY_REQUEST_CODE)) arguments!!.getInt(KEY_REQUEST_CODE) else targetRequestCode

    private lateinit var inputField:EditText

    /**
     * InputDialogコールバックリスナ
     */
    interface CallBack{
        fun onMyDialogSuccess(requestCode:Int,resultCode:Int,input:String)
        fun onMyDialogCancelled(requestCode: Int)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val callback = parentFragment?:activity?:throw IllegalStateException()
        mCallback = callback as CallBack
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val onDialogListener:DialogInterface.OnClickListener? = DialogInterface.OnClickListener { _, which ->
            dismiss()
            mCallback.onMyDialogSuccess(requestCode,which,inputField.text.toString())
        }
        val onCancelListener:DialogInterface.OnClickListener? = DialogInterface.OnClickListener { dialogInterface, which ->
            dismiss()
            mCallback.onMyDialogCancelled(which)
        }

        val title = arguments!!.getString(KEY_TITLE)
        val message = arguments!!.getString(KEY_MESSAGE)
        val items = arguments!!.getStringArray(KEY_ITEMS)
        val positiveLabel = arguments!!.getString(KEY_POSITIVE)
        val negativeLabel = arguments!!.getString(KEY_NEGATIVE)
        isCancelable = arguments!!.getBoolean(KEY_CANCELABLE)

        inputField = EditText(context!!)
        inputField.setEms(3)
        inputField.inputType = EditorInfo.TYPE_CLASS_NUMBER

        /** DialogのBuilderを生成　*/
        val builder = AlertDialog.Builder(context!!)

        /**
         * 取得したプロパティをBuilderにセット
         */
        if(!TextUtils.isEmpty(title))
            builder.setTitle(title)

        if (!TextUtils.isEmpty(message))
            builder.setMessage(message)

        if (items != null && items.isNotEmpty())
            builder.setItems(items,onDialogListener)

        if (!TextUtils.isEmpty(positiveLabel))
            builder.setPositiveButton(positiveLabel,onDialogListener)

        if (!TextUtils.isEmpty(negativeLabel))
            builder.setNegativeButton(negativeLabel, onCancelListener)

        builder.setView(inputField)

        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        mCallback.onMyDialogCancelled(requestCode)
    }

    class Builder{
        /** Activity */
        internal val activity: AppCompatActivity?

        /** 生成元フラグメント*/
        internal val parentFragment: Fragment?

        /** タイトル */
        internal  var title:String? = null
        /** メッセージ　*/
        internal  var message:String? = null
        /** 表示アイテム */
        internal var items:Array<String>? = null
        /** positiveButton用Label */
        internal var positiveLabel:String? = null
        /** negativeButton用Label */
        internal var negativeLabel:String? = null
        /** requestCode */
        internal var requestCode = -1
        /** Dialog設定用プロパティ格納Bundle */
        internal lateinit var args:Bundle
        /** 生成元ContextTag */
        internal var tag:String? = null
        /** キャンセル可能フラグ */
        internal var cancelable = true

        /**
         * 生成元のContextを取得
         * ContextからgetStringでプロパティを取得する際に必要
         */
        private val context:Context
            get() = (activity?:parentFragment?.context) ?:throw IllegalStateException()

        /**
         * コンストラクタ
         * Activityから生成時
         * @param activity 生成元Activity
         */
        constructor(activity:AppCompatActivity){
            this.activity = activity
            parentFragment = null
        }

        /**
         * コンストラクタ
         * Fragmentから生成時
         *
         * @param parentFragment 生成元Fragment
         */
        constructor(parentFragment:Fragment){
            this.parentFragment = parentFragment
            activity = null
        }

        fun title(title:String): Builder {
            this.title = title
            return this
        }

        fun title(@StringRes title:Int): Builder = title(context.getString(title))

        fun message(message:String): Builder {
            this.message = message
            return this
        }

        fun message(@StringRes message:Int): Builder = message(context.getString(message))

        fun positiveLabel(positiveLabel:String): Builder {
            this.positiveLabel = positiveLabel
            return this
        }

        fun item(items:Array<String>): Builder {
            this.items = items
            return this
        }

        fun positiveLabel(@StringRes positiveLabel:Int): Builder = positiveLabel(context.getString(positiveLabel))

        fun negativeLabel(negativeLabel:String): Builder {
            this.negativeLabel = negativeLabel
            return this
        }

        fun negativeLabel(@StringRes negativeLabel:Int): Builder = negativeLabel(context.getString(negativeLabel))

        fun requestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun tag(tag:String): Builder {
            this.tag = tag
            return this
        }

        fun args(args:Bundle): Builder {
            this.args = args
            return this
        }

        fun cancelable(cancelable:Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        /**
         * Dialog表示関数
         */
        fun show(){

            /**
             * BundleにDialogプロパティをセット
             */
            val arguments = Bundle()
            arguments.run {
                putString(KEY_TITLE,title)
                putString(KEY_MESSAGE,message)
                putStringArray(KEY_ITEMS,items)
                putString(KEY_POSITIVE,positiveLabel)
                putString(KEY_NEGATIVE,negativeLabel)
                putBoolean(KEY_CANCELABLE,cancelable)
                putBundle(KEY_ARGS,args)

            }

            val fragment = InputDialogFragment()
            if (parentFragment != null) {
                fragment.setTargetFragment(parentFragment,requestCode)
            }else
                args.putInt(KEY_REQUEST_CODE,requestCode)

            fragment.arguments = arguments
            if (parentFragment != null)
                fragment.show(parentFragment.fragmentManager,tag)
            else
                fragment.show(activity?.supportFragmentManager,tag)
        }
    }

}
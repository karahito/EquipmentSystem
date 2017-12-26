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

package jms.mobile

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.wifi.WifiInfo
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.InputFilter
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import jms.android.camera.barcode.BarcodeCaptureActivity
import jms.android.common.LogUtil
import jms.android.common.utility.RxNetworkState
import jms.mobile.Entity.DBTable.EquipmentHolder
import jms.mobile.Model.DbManagement
import jms.mobile.Model.EventStream
import jms.mobile.Model.NetworkProtocol
import jms.mobile.UI.EquipHolder.HolderContentsFragment
import jms.mobile.UI.History.HistoryContentsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity(){

    /**
     * Callback Listener
     */
    /**==================================================================================================================================*/

    /** Navigation Callback */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_list -> {
                manager.beginTransaction()
                        .replace(R.id.contents, holder)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                onBarcodeScanClick()
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_history -> {
                manager.beginTransaction()
                        .replace(R.id.contents,history)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /** Dialog Callback onPositive */
    private val mDialogListener = DialogInterface.OnClickListener { _, _ ->
        val input = inputField.text.toString()
        inputField.text.clear()

        if(netStatus) {
            client.login(input)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .bindToLifecycle(this)
                    .doOnSubscribe {
                        progress.setCancelable(false)
                        progress.show()
                    }.doOnDispose {
                LogUtil.d("$client will be dispose")
            }
                    .subscribe(
                            {
                                client.user = it
                                client.wCode = input
                                mUser.text = client.user
                                progress.dismiss()
                                Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
                                toolbar.menu.getItem(MenuItem.SIGN_OUT.position).isEnabled = true
                            },
                            {
                                client.user = null
                                client.wCode = null
                                mUser.text = this.getText(R.string.login_unless)
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                                progress.dismiss()
                                dialog.show()
                                toolbar.menu.getItem(MenuItem.SIGN_OUT.position).isEnabled = false
                            }
                    )
        }else{
            client.user = input
            client.wCode = input
            mUser.text = input
            toolbar.menu.getItem(MenuItem.SIGN_OUT.position).isEnabled = true
        }
    }
    /**==================================================================================================================================*/

    @BindView(R.id.login_user) lateinit var mUser:TextView
    @BindView(R.id.switchWidget) lateinit var mSwitch:Switch
    @BindView(R.id.network_status) lateinit var mNetwork:TextView

    @Suppress("DEPRECATION")
    private lateinit var progress:ProgressDialog
    private lateinit var inputField:EditText
    private lateinit var dialog:AlertDialog
    private var netStatus:Boolean = true


    /**
     * ActionBarOptionMenu生成
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        /** MenuItemClickEventListener */
        toolbar.setOnMenuItemClickListener {
            if(it.itemId == R.id.signIn) {
                dialog.show()
            }else{
                client.user = null
                client.wCode = null
                mUser.text = this.getText(R.string.login_unless)
                toolbar.menu.getItem(MenuItem.SIGN_OUT.position).isEnabled = false
            }
            true
        }
        return true
    }


    /**
     * Activityの生成、初期化処理
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** viewに描画するLayoutをセット*/
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        DbManagement.openDb()
        progress = ProgressDialog(this)
        inputField = EditText(this)

        EventStream.run()
        setNetworkStatus(isWifiStatus(network.getNetworkInfo(this)))

        val maxTextLength = 3
        val lengthFilter = InputFilter.LengthFilter(maxTextLength)
        inputField.filters = arrayOf<InputFilter>(lengthFilter)
        inputField.inputType = EditorInfo.TYPE_CLASS_NUMBER

        dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.login_jpn))
                .setMessage("社員番号を入力してください")
                .setView(inputField)
                .setPositiveButton("Login.",mDialogListener)
                .setNegativeButton("cancel.",null)
                .create()

        /** toolbarをActionBarとしてセット */
        setSupportActionBar(toolbar)

        /** Navigationのリスナーをアクティベイト*/
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        mSwitch.setOnCheckedChangeListener { _, boolean ->
            if(!boolean){
                mNetwork.run {
                    text = OFFLINE
                    setTextColor(ContextCompat.getColor(this.context,R.color.failed))
                }
                netStatus = boolean
            }else
                setNetworkStatus(boolean)

            val args = Bundle()
            args.putBoolean(getString(R.string.NetState_key),netStatus)
            holder.arguments = args

        }

        manager = supportFragmentManager
        manager.beginTransaction()
                .add(R.id.contents, holder)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_CAPTURE_REQUEST) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    val result = barcode.displayValue
                    val eCode = result.lines()
                    if (result.lines().count() == 3 && eCode[0].length == 5) {
                        val realm = Realm.getDefaultInstance()
                        realm.run {
                            beginTransaction()
                            insertOrUpdate(EquipmentHolder(eCode[0], eCode[1], eCode[2]))
                            commitTransaction()
                        }
                        realm.close()
                        Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "バーコードがキャプチャ出来ませんでした", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "バーコードの読み込みに失敗しました", Toast.LENGTH_SHORT).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DbManagement.closeDb()
    }

    override fun onResume() {
        super.onResume()
        subscribeNetworkState()
        network.registerReceiver(this)
    }

    override fun onPause() {
        super.onPause()
        network.unregisterReceiver(this)
        disposable.dispose()
    }

    private fun subscribeNetworkState(){

        network.subject
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        /** onNext */
                        {
                            setNetworkStatus(isWifiStatus(it))
                        },
                        /** onError */
                        {
                            setNetworkStatus(false)
                        }
                )
                .addTo(disposable)
    }

    private fun isWifiStatus(info:Any?):Boolean{
        return info is WifiInfo && info.ssid == ACCEPT_SSID
    }

    private fun setNetworkStatus(isWifi:Boolean){
        when(isWifi){
            true->{
                mNetwork.run {
                    text = ONLINE
                    setTextColor(ContextCompat.getColor(this.context,R.color.success))
                }
                mSwitch.run {
                    isEnabled = true
                    isChecked = true
                }
                netStatus = true
            }
            false ->{
                mNetwork.run {
                    text = OFFLINE
                    setTextColor(ContextCompat.getColor(this.context,R.color.failed))
                }
                mSwitch.run {
                    isChecked = false
                    isEnabled = false
                }
                netStatus = false
            }
        }
        val args = Bundle()
        args.putBoolean(getString(R.string.NetState_key),netStatus)
        holder.arguments = args
    }

    private fun onBarcodeScanClick() {
        val intent = Intent(this, BarcodeCaptureActivity::class.java)
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true)
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false)

        startActivityForResult(intent, BARCODE_CAPTURE_REQUEST)
    }


    /**
     * SingletonObject を定義
     */
    companion object {
        @JvmStatic lateinit var manager:FragmentManager
        @JvmStatic val holder = HolderContentsFragment.newInstance()
        @JvmStatic val history = HistoryContentsFragment.newInstnce()
        @JvmStatic val client = NetworkProtocol
        enum class MenuItem(val position:Int){
            SIGN_IN(0),
            SIGN_OUT(1)
        }
        private val ONLINE = "ONLINE"
        private val OFFLINE = "OFFLINE"
        private val ACCEPT_SSID = """"JMS-SOFT""""
        @JvmStatic val network = RxNetworkState.newInstance()
        @JvmStatic val disposable = CompositeDisposable()
        private val BARCODE_CAPTURE_REQUEST = 0x0001
    }
}

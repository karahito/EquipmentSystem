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

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import jms.android.common.LogUtil
import jms.mobile.Entity.LoginInput

/**
 *
 */
object EventStream {
    private val stream:PublishSubject<Any> = PublishSubject.create()
    private val disposable = CompositeDisposable()
    fun getStream():Subject<Any> = stream

    fun run(){

        stream.filter{it is LoginInput}
                .subscribe(
                {
                    LogUtil.d(it.toString())
                })
                .addTo(disposable)
    }

    fun stop(){
        disposable.dispose()
    }
}
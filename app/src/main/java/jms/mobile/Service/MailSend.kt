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

package jms.mobile.Service

import io.reactivex.Single
import jms.android.mail.AndroidJavaMail

/**
 *
 */
class MailSend {
   fun build(mailer:AndroidJavaMail):Single<Unit>{
       return Single.create {
           try {
               if (mailer.send())
                   it.onSuccess(Unit)
               else
                   it.onError(ClassCastException("Failed"))
           }catch (e:Exception){
               it.onError(e)
           }
       }
   }
}
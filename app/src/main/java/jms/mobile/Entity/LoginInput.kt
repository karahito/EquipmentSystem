

package jms.mobile.Entity

/**
 *
 */
data class LoginInput(
        val userCode:String,
        val wifiState:Boolean,
        val MAX_LENGTH: Int = 5
)
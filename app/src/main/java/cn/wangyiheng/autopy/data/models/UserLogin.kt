package cn.wangyiheng.autopy.data.models

data class VerificationRequest(val tel: String)
data class VerificationResponse(val code:String,val msg:String)
data class LoginRequest(val tel: String, val sms_code: String)
data class LoginResponse(val code:String,val msg:String,val token:String)


data class ActionResult(val success: Boolean, val message: String, val token: String? = null)
package cn.wangyiheng.autopy.shared.utils

import android.util.Log
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): T? {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()
        } else {
            Log.d("API Error", response.errorBody()?.string() ?: "Unknown error")
            null
        }
    } catch (e: Exception) {
        Log.d("API Exception", e.toString())
        null
    }
}
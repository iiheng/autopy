package cn.wangyiheng.autopy.shared.utils

import android.content.Context
import com.google.gson.Gson

class InfoManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun removeToken() {
        sharedPreferences.edit().remove("token").apply()
    }

}
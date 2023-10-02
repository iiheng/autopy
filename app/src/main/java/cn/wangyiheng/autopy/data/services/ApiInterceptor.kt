package cn.dianbobo.dbb.data.services


import cn.wangyiheng.autopy.shared.utils.InfoManager
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject



class ApiInterceptor(private val InfoManager: InfoManager) : Interceptor {
    private val tokenExcludedUrls = mapOf(
        "/v1/user/sms" to true,
        "/v1/user/login" to true
    )
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = InfoManager.getToken()

        if (token != null && !tokenExcludedUrls.containsKey(originalRequest.url().toString())) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "$token")
                .build()
            return chain.proceed(newRequest)
        }

        val response = chain.proceed(originalRequest)

        val responseBodyString = response.body()?.string() ?: return response
        if (responseBodyString.isBlank()) return response // 检查字符串是否为空
        val json = JSONObject(responseBodyString)
        val code = json.optString("code", "")

        val customMessage = if (code == "200") "Request Successful based on response body" else "Request was not successful based on response body"

        val newResponseBody = okhttp3.ResponseBody.create(response.body()?.contentType(), responseBodyString)

        return response.newBuilder()
            .body(newResponseBody)
            .addHeader("Custom-Message", customMessage)
            .build()
    }
}
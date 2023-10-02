package cn.dianbobo.dbb.data.services;


import cn.wangyiheng.autopy.data.models.*
import retrofit2.Response;
import retrofit2.http.*;

// 定义与后端API交互的接口
interface ApiService {
    //---------------------------------------------------------------------------------
    // 用户登录接口
    @POST("/v1/user/sms")
    suspend fun sendVerificationCode(@Body data: VerificationRequest): Response<VerificationResponse>

    @POST("/v1/user/login")
    suspend fun userLogin(@Body data: LoginRequest): Response<LoginResponse>

    // 获取主播接口


}
package cn.wangyiheng.autopy.data.services

import cn.dianbobo.dbb.data.services.ApiInterceptor
import cn.dianbobo.dbb.data.services.ApiService
import cn.wangyiheng.autopy.shared.utils.InfoManager
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://124.223.181.243:8079"

val networkModule = module {

    factory { ApiInterceptor(get<InfoManager>()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<ApiInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get<OkHttpClient>())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }
}
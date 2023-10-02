package cn.wangyiheng.autopy

import android.app.Application
import cn.wangyiheng.autopy.data.services.networkModule
import cn.wangyiheng.autopy.data.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            // Declare modules to use
            androidContext(this@MyApplication)
            modules(appModule, networkModule)
        }
    }
}
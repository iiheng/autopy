package cn.wangyiheng.autopy.data.di

import cn.wangyiheng.autopy.shared.utils.InfoManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import cn.wangyiheng.autopy.services.auto.Auto

val appModule = module {
//    single { androidContext() }
    single { InfoManager(androidContext()) }

    single{ Auto(androidContext()) }

}
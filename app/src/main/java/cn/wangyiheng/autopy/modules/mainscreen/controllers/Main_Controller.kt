package cn.wangyiheng.autopy.modules.mainscreen.controllers

import androidx.lifecycle.ViewModel
import cn.wangyiheng.autopy.services.auto.Auto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
class Main_Controller: ViewModel(), KoinComponent {
    val auto: Auto by inject()
    fun start() {
        auto.ensureAccessibilityServiceEnabled()
    }


}
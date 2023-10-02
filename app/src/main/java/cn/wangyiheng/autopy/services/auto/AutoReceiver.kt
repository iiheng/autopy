package cn.wangyiheng.autopy.services.auto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AutoReceiver(private val onActionReceived: suspend (AutoAction, Intent) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = AutoAction.values().find { it.action == intent.action }
        action?.let {
            CoroutineScope(Dispatchers.Default).launch {
                withContext(Dispatchers.Default) {
                    onActionReceived(it, intent)
                }
            }
        }
    }
}
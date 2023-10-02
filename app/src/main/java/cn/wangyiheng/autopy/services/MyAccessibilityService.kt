package cn.wangyiheng.autopy.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import cn.wangyiheng.autopy.services.auto.AccessibilityInfoManager
import cn.wangyiheng.autopy.services.auto.AutoAction
import cn.wangyiheng.autopy.services.auto.AutoReceiver
import cn.wangyiheng.autopy.shared.bus.RxBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyAccessibilityService : AccessibilityService() {
    private val receiver = AutoReceiver { action, intent ->
        try {
            when (action) {
                AutoAction.CLICK, AutoAction.SWIPE -> handleGesture(intent)
                AutoAction.GETINFO, AutoAction.GETINFOS -> handleInfoRetrieval(intent)
                AutoAction.BACK -> handleBack()
            }
        } catch (e: Exception) {
            // TODO: Handle or log the exception accordingly
        }
    }

    private fun handleBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    // 点击和滑动的
    private suspend fun handleGesture(intent: Intent) = withContext(Dispatchers.Default) {
        try {
            val x1 = intent.getIntExtra("x", intent.getIntExtra("x1", 0)).toFloat()
            val y1 = intent.getIntExtra("y", intent.getIntExtra("y1", 0)).toFloat()
            val x2 = intent.getIntExtra("x2", x1.toInt()).toFloat()
            val y2 = intent.getIntExtra("y2", y1.toInt()).toFloat()
            val duration = intent.getIntExtra("duration", 200).toLong()

            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x2, y2)
            }

            val gestureDescription = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0L, duration))
                .build()

            dispatchGesture(gestureDescription, null, null)
        } catch (e: Exception) {
            // TODO: Handle or log the exception accordingly
        }
    }


    private suspend fun handleInfoRetrieval(intent: Intent) = withContext(Dispatchers.Default) {
        val key = intent.getStringExtra("key") ?: return@withContext
        val value = intent.getStringExtra("value") ?: return@withContext
        val depth = intent.getIntExtra("depth", 0)

        val isMultiple = intent.action == AutoAction.GETINFOS.action
        val nodes = if (isMultiple) getNodeInfos(depth, key, value) else listOf(getNodeInfo(depth, key, value))

        val resultData = Bundle().apply {
            if (isMultiple) putParcelableArrayList("nodes", ArrayList(nodes.filterNotNull()))
            else putParcelable("node", nodes.firstOrNull())
        }

        val uniqueIdFromIntent = intent.getLongExtra("uniqueId", -1)

        // 使用 RxBus 发布事件
        RxBus.post(cn.wangyiheng.autopy.services.auto.AccessibilityEvent(uniqueIdFromIntent, resultData))
    }

    private fun getNodeInfo(depth: Int, key: String, value: String): AccessibilityNodeInfo? =
        traverseForNodes(depth, key, value).firstOrNull()

    private fun getNodeInfos(depth: Int, key: String, value: String): List<AccessibilityNodeInfo> =
        traverseForNodes(depth, key, value)

    private fun traverseForNodes(depth: Int, key: String, value: String): List<AccessibilityNodeInfo> {
        val matchedNodes = mutableListOf<AccessibilityNodeInfo>()

        for (window in windows) {
            when (window.type) {
                AccessibilityWindowInfo.TYPE_APPLICATION,
                AccessibilityWindowInfo.TYPE_INPUT_METHOD,
                AccessibilityWindowInfo.TYPE_SYSTEM -> {
                    val rootNode = window.root
                    if (rootNode != null) {
                        matchedNodes.addAll(traverseNodes(rootNode, depth, key, value))
                    }
                }
            }
        }

        return matchedNodes
    }

    private fun traverseNodes(node: AccessibilityNodeInfo?, depth: Int, key: String, value: String): MutableList<AccessibilityNodeInfo> {
        val matchedNodes = mutableListOf<AccessibilityNodeInfo>()

        node?.let {
            when (key) {
                "text" -> if (node.text?.toString() == value) matchedNodes.add(node)
                "desc" -> if (node.contentDescription?.toString() == value) matchedNodes.add(node)
                "packageName" -> if (node.packageName?.toString() == value) matchedNodes.add(node)
                "className" -> if (node.className?.toString() == value) matchedNodes.add(node)
                "fullId" -> if (node.viewIdResourceName == value) matchedNodes.add(node)
                // TODO: 可以继续添加更多属性的检查
            }
            for (i in 0 until it.childCount) {
                matchedNodes.addAll(traverseNodes(it.getChild(i), depth + 1, key, value))
            }
        }
        return matchedNodes
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onServiceConnected() {
        super.onServiceConnected()

        val filter = IntentFilter().apply {
            AutoAction.values().forEach {
                addAction(it.action)
            }
        }
        registerReceiver(receiver, filter)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityInfoManager.currentPackageName = event.packageName.toString()
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver) // 注销广播接收器
    }
}
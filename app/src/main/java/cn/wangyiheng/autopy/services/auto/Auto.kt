package cn.wangyiheng.autopy.services.auto

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import cn.wangyiheng.autopy.services.MyAccessibilityService
import cn.wangyiheng.autopy.shared.bus.RxBus
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

var getinfocount = 0
var getinfoscount = 0
class Auto(private val context: Context) {

    fun click(x: Int, y: Int,duration:Int = 300): Boolean {
        val intent = Intent(AutoAction.CLICK.action).apply {
            putExtra("x", x)
            putExtra("y", y)
            putExtra("duration", duration)
        }
        context.sendBroadcast(intent)
        return true
    }

    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int,duration:Int): Boolean {
        val intent = Intent(AutoAction.SWIPE.action).apply {
            putExtra("x1", x1)
            putExtra("y1", y1)
            putExtra("x2", x2)
            putExtra("y2", y2)
            putExtra("duration", duration)
        }
        context.sendBroadcast(intent)
        return true
    }
    fun back(): Boolean {
        val intent = Intent(AutoAction.BACK.action)
        context.sendBroadcast(intent)
        return true
    }
    fun pressEnter(): Boolean {
        val intent = Intent(AutoAction.ENTER.action)
        context.sendBroadcast(intent)
        return true
    }

    fun setText(node: AccessibilityNodeInfo?, text: String): Boolean {
        if (node == null || !node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT)) {
//            node?.recycle()  // 如果节点不为null，确保回收节点以避免内存泄漏
            return false  // 返回false如果节点是null或不支持设置文本操作
        }
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text
        )
        //        node.recycle()  // 重要：在完成后回收节点以避免内存泄漏
        return node.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT, arguments
        )  // 返回操作是否成功
    }
    fun launchApp(packageName: String) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            // Handle the case where the app is not installed or there's no launch intent
            Toast.makeText(context, "没有找到相关应用", Toast.LENGTH_SHORT).show()
        }
    }
    fun getCurrentPackageName(): String? {
        return AccessibilityManager.currentPackageName
    }

    @SuppressLint("ServiceCast")
    public fun ensureAutoEnabled(accessibilityService: String = MyAccessibilityService::class.java.name): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        if (enabledServices?.contains(accessibilityService) != true) {
            // 如果没有启动，引导用户启动无障碍服务
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            return false
        }else{
            return true
        }
    }

    suspend fun getNoteInfo(
        depth: Int,
        key: String,
        value: String,
        timeout: Int = 0
    ): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
        getinfocount++
        Log.d("m", getinfocount.toString())
        var node: AccessibilityNodeInfo? = null
        var disposable: Disposable? = null

        val random = Random.nextInt(0, 1000)
        val uniqueId = "$depth${key.length}${value.length}$timeout$getinfoscount$random".toLong()  // 生成唯一的ID
        try {
            var receivedMessage = false
            disposable = RxBus.toObservable(AccessibilityEvent::class.java)
                .filter { event -> event.uniqueId == uniqueId }  // 仅筛选与此请求ID匹配的事件
                .subscribe { event ->
                    node = event.resultData?.getParcelable("node")
                    receivedMessage = true
                    if (node != null) {
                        disposable?.dispose()
                    }
                }

            val intent = Intent(AutoAction.GETINFO.action).apply {
                putExtra("depth", depth)
                putExtra("key", key)
                putExtra("value", value)
                putExtra("uniqueId", uniqueId)
            }

            val startTime = System.currentTimeMillis()

            while (node == null && (timeout <= 0 || System.currentTimeMillis() - startTime < timeout)) {
                if (receivedMessage || System.currentTimeMillis() - startTime == 0L) {
                    context.sendBroadcast(intent)
                    receivedMessage = false
                }
                delay(50)
            }
        } catch (e: Exception) {
            // Handle or log the exception
        } finally {
            disposable?.dispose()
        }
        return@withContext node
    }
    fun getDeviceWidthAndHeight(): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        return Pair(width, height)
    }
    suspend fun getNoteInfos(
        depth: Int,
        key: String,
        value: String,
        timeout: Int = 0
    ): List<AccessibilityNodeInfo>? = withContext(Dispatchers.Default) {
        getinfoscount++
        var nodes: List<AccessibilityNodeInfo>? = null
        var disposable: Disposable? = null
        val random = Random.nextInt(0, 1000)
        val uniqueId = "$depth${key.length}${value.length}$timeout$getinfoscount$random".toLong() // 1. 生成唯一的ID
        try {
            var receivedMessage = false
            disposable = RxBus.toObservable(AccessibilityEvent::class.java)
                .filter { event -> event.uniqueId == uniqueId }  // 3. 在订阅时根据这个ID进行筛选
                .subscribe { event ->
                    nodes = event.resultData?.getParcelableArrayList("nodes")
                    receivedMessage = true
                    if (nodes != null) {
                        disposable?.dispose()
                    }
                }

            val intent = Intent(AutoAction.GETINFOS.action).apply {
                putExtra("depth", depth)
                putExtra("key", key)
                putExtra("value", value)
                putExtra("uniqueId", uniqueId)  // 2. 将此ID作为事件的一部分
            }

            val startTime = System.currentTimeMillis()
            while (nodes == null && (timeout <= 0 || System.currentTimeMillis() - startTime < timeout)) {
                if (receivedMessage || System.currentTimeMillis() - startTime == 0L) {
                    context.sendBroadcast(intent)
                    receivedMessage = false
                }
                delay(50)
            }
        } catch (e: Exception) {
            // Handle or log the exception
        } finally {
            disposable?.dispose()
        }
        return@withContext nodes
    }

    suspend fun getBounds(depth: Int, key: String, value: String, timeout: Int = 0): Rect? {
        val resultData = getNoteInfo(depth, key, value, timeout)

        var bounds: Rect? = null

        resultData?.let {
            bounds = Rect()
            it.getBoundsInScreen(bounds)
        } ?: run {
            Log.d("Error", "No component found!")
        }

        return bounds
    }

    fun getBounds(nodeInfo: AccessibilityNodeInfo): Rect? {
        var bounds: Rect? = null
        nodeInfo.let {
            bounds = Rect()
            it.getBoundsInScreen(bounds)
        }
        return bounds
    }

    suspend fun widgetClick(depth: Int, key: String, value: String, timeout: Int = 0, pressTime: Int = 300): Boolean {
        val resultData = getNoteInfo(depth, key, value, timeout) ?: return false
        var clicked = false
        try {
            if (resultData.isClickable) {
                resultData.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("Clicked", "Successfully clicked the node!")
                clicked = true
            } else {
                val parentNode = resultData.parent
                if (parentNode?.isClickable == true) {
                    parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.d("Clicked", "Successfully clicked the parent node!")
                    clicked = true
                    parentNode.recycle() // 释放 parent node
                } else {
                    resultData.let {
                        val bounds = Rect()
                        it.getBoundsInScreen(bounds)
                        click(bounds.centerX(), bounds.centerY(), pressTime)
                        Log.d("Clicked", "Clicked on the component's bounds!")
                        clicked = true
                    }
                }
            }
        } finally {
            resultData.recycle() // 释放 resultData node
        }
        return clicked
    }

    suspend fun widgetClick(nodeInfo: AccessibilityNodeInfo, pressTime: Int = 300): Boolean {
        var clicked = false
        try {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("Clicked", "Successfully clicked the node!")
                clicked = true
            } else {
                val parentNode = nodeInfo.parent
                if (parentNode?.isClickable == true) {
                    parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.d("Clicked", "Successfully clicked the parent node!")
                    clicked = true
                } else {
                    val bounds = Rect()
                    nodeInfo.getBoundsInScreen(bounds)
                    click(bounds.centerX(), bounds.centerY(), pressTime)
                    Log.d("Clicked", "Clicked on $nodeInfo the component's bounds!")
                    clicked = true
                }
                parentNode?.recycle() // 释放 parent node
            }
        } catch (e: Exception) {
            // 处理异常
            Log.e("Error", "An error occurred: ${e.message}")
        }
        return clicked
    }
}
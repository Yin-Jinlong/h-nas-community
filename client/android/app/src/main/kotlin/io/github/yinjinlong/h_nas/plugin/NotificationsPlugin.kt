package io.github.yinjinlong.h_nas.plugin

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class NotificationsPlugin(
    val channel: MethodChannel,
    val hasPermission: () -> Boolean,
    val requestPermission: () -> Unit,
    val postNotification: (title: String, subText: String, playing: Boolean) -> Unit,
    val closeNotification: () -> Unit,
) : MethodChannel.MethodCallHandler {

    companion object {
        const val NAME = "notifications_plugin"
        var instance: NotificationsPlugin? = null
    }

    var result: MethodChannel.Result? = null

    init {
        instance = this
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        if (requestCode != 0)
            return
        result?.success(hasPermission())
    }

    override fun onMethodCall(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when (call.method) {
            "hasPermission" -> result.success(hasPermission())
            "requestPermission" -> {
                this.result = result
                requestPermission()
            }

            "showPlayerNotification" -> {
                val args: List<*> = call.arguments as List<*>
                val title = args[0] as String
                val subText = args[1] as String
                val playing = args[2] as Boolean
                postNotification(title, subText, playing)
                result.success(true)
            }

            "close" -> {
                closeNotification()
                result.success(true)
            }

            else -> result.notImplemented()
        }
    }

    fun onPlayPause() {
        channel.invokeMethod("onPlayPause", null)
    }

    fun onClose() {
        channel.invokeMethod("onClose", null)
    }
}
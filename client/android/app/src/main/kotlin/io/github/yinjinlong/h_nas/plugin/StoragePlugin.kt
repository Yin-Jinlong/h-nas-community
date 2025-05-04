package io.github.yinjinlong.h_nas.plugin

import android.app.Activity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

/**
 * @author YJL
 */
class StoragePlugin(
    private val activity: Activity,
) : MethodChannel.MethodCallHandler {
    companion object {
        const val NAME = "storage_plugin"
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getAppSize" -> result.success(getAppSize())
        }
    }

    private fun getAppSize(): Long {
        val pm = activity.packageManager
        val info = pm.getPackageInfo(activity.packageName, 0)
        val path = info.applicationInfo?.sourceDir ?: return 1
        return File(path).length()
    }
}
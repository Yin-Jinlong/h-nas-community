package io.github.yinjinlong.h_nas

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setHighRefreshRate()
        super.onCreate(savedInstanceState)
    }

    private fun setHighRefreshRate() {
        val mode = display.supportedModes.find { it.refreshRate >= 90 } ?: return
        val params = window.attributes
        params.preferredDisplayModeId = mode.modeId
        window.attributes = params
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, BroadcastPlugin.NAME)
            .setMethodCallHandler(
                BroadcastPlugin()
            )
    }
}

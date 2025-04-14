package io.github.yinjinlong.h_nas

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.github.yinjinlong.h_nas.plugin.BroadcastPlugin
import io.github.yinjinlong.h_nas.plugin.NotificationsPlugin

class MainActivity : FlutterActivity() {

    lateinit var notificationsPlugin: NotificationsPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        setHighRefreshRate()
        initNotifications()
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
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, NotificationsPlugin.NAME).apply {
            setMethodCallHandler(NotificationsPlugin(this, this@MainActivity).apply {
                notificationsPlugin = this
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        notificationsPlugin.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

    fun initNotifications() {
        val manager = NotificationManagerCompat.from(this)
        manager.deleteUnlistedNotificationChannels(listOf(Notifications.CHANNEL_MUSIC_PLAYER))

        manager.notificationChannelGroups.forEach {
            if (it.id != Notifications.GROUP_STATUS) {
                manager.deleteNotificationChannelGroup(it.id)
            }
        }

        manager.createNotificationChannelGroup(
            NotificationChannelGroupCompat.Builder(
                Notifications.GROUP_STATUS
            ).setName("状态")
                .setDescription("应用状态")
                .build()
        )

        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(Notifications.CHANNEL_MUSIC_PLAYER, NotificationManager.IMPORTANCE_LOW)
                .setName("音乐播放")
                .setDescription("音乐播放信息")
                .build()
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        when (intent.action) {
            NotificationsPlugin.ACTION_PLAY_PAUSE -> notificationsPlugin.onPlayPause()

            NotificationsPlugin.ACTION_CLOSE -> notificationsPlugin.onClose()
        }
    }
}

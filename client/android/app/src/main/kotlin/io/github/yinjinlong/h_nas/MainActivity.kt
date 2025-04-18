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
import io.github.yinjinlong.h_nas.service.MusicControlService

class MainActivity : FlutterActivity() {

    lateinit var notificationsPlugin: NotificationsPlugin
    lateinit var notificationsManager: NotificationManagerCompat

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
            setMethodCallHandler(
                NotificationsPlugin(
                    this,
                    ::hasNotificationPermission,
                    ::requestNotificationPermission,
                    ::postNotification,
                    ::closeNotification
                ).apply {
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
        notificationsManager = NotificationManagerCompat.from(this)
        notificationsManager.deleteUnlistedNotificationChannels(listOf(Notifications.CHANNEL_MUSIC_PLAYER))

        notificationsManager.notificationChannelGroups.forEach {
            if (it.id != Notifications.GROUP_STATUS) {
                notificationsManager.deleteNotificationChannelGroup(it.id)
            }
        }

        notificationsManager.createNotificationChannelGroup(
            NotificationChannelGroupCompat.Builder(
                Notifications.GROUP_STATUS
            ).setName("状态")
                .setDescription("应用状态")
                .build()
        )

        notificationsManager.createNotificationChannel(
            NotificationChannelCompat.Builder(Notifications.CHANNEL_MUSIC_PLAYER, NotificationManager.IMPORTANCE_LOW)
                .setName("音乐播放")
                .setDescription("音乐播放信息")
                .build()
        )
    }

    fun hasNotificationPermission() = notificationsManager.areNotificationsEnabled()
    fun requestNotificationPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.POST_NOTIFICATIONS
            ), 0
        )
    }

    fun postNotification(title: String, artists: String, cover: String?, playing: Boolean) {
        startForegroundService(Intent(activity, MusicControlService::class.java).apply {
            action = MusicControlService.ACTION_UPDATE
            putExtra(MusicControlService.EXTRA_TITLE, title)
            putExtra(MusicControlService.EXTRA_ARTIST, artists)
            putExtra(MusicControlService.EXTRA_COVER, cover)
            putExtra(MusicControlService.EXTRA_PLAYING, playing)
        })
    }

    fun closeNotification() {
        stopService(Intent(activity, MusicControlService::class.java))
    }
}

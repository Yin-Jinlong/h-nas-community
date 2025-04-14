package io.github.yinjinlong.h_nas.plugin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.github.yinjinlong.h_nas.MainActivity
import io.github.yinjinlong.h_nas.Notifications
import io.github.yinjinlong.h_nas.R
import io.github.yinjinlong.h_nas.utils.isDark

class NotificationsPlugin(
    val channel: MethodChannel,
    val activity: Activity
) : MethodChannel.MethodCallHandler {

    companion object {
        const val NAME = "notifications_plugin"
        const val ACTION_PLAY_PAUSE = "play_pause"
        const val ACTION_CLOSE = "close"
    }

    val notificationManager = NotificationManagerCompat.from(activity)

    var result: MethodChannel.Result? = null

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
                showPlayerNotification(title, subText, playing)
                result.success(true)
            }

            "removePlayerNotification" -> {
                removePlayerNotification()
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    fun hasPermission() = notificationManager.areNotificationsEnabled()

    fun requestPermission() {
        if (hasPermission())
            return
        activity.requestPermissions(
            arrayOf(
                android.Manifest.permission.POST_NOTIFICATIONS
            ), 0
        )
    }

    @SuppressLint("MissingPermission")
    fun showPlayerNotification(
        title: String,
        subText: String,
        playing: Boolean
    ) {
        if (!hasPermission())
            return
        val view = RemoteViews(activity.packageName, R.layout.music_layout)

        view.setTextViewText(R.id.music_title, title)
        view.setTextViewText(R.id.music_artists, subText)


        val color = (if (activity.isDark) 0xfffafafa else 0xff212121).toInt()

        view.setImageViewIcon(
            R.id.music_play_pause,
            Icon.createWithResource(
                activity,
                if (playing) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24
            ).apply {
                setTint(color)
            }
        )
        view.setImageViewIcon(R.id.music_close, Icon.createWithResource(activity, R.drawable.round_close_24).apply {
            setTint(color)
        })
        view.setOnClickPendingIntent(
            R.id.music_play_pause,
            PendingIntent.getActivity(
                activity,
                0,
                Intent(activity, MainActivity::class.java).apply {
                    action = ACTION_PLAY_PAUSE
                },
                PendingIntent.FLAG_MUTABLE
            )
        )
        view.setOnClickPendingIntent(
            R.id.music_close,
            PendingIntent.getActivity(
                activity,
                0,
                Intent(activity, MainActivity::class.java).apply {
                    action = ACTION_CLOSE
                },
                PendingIntent.FLAG_MUTABLE
            )
        )

        val notification = NotificationCompat.Builder(activity, Notifications.CHANNEL_MUSIC_PLAYER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setCustomContentView(view)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()

        notificationManager.notify(0, notification)
    }

    fun removePlayerNotification() {
        notificationManager.cancel(0)
    }

    fun onPlayPause() {
        channel.invokeMethod("onPlayPause", null)
    }

    fun onClose() {
        channel.invokeMethod("onClose", null)
    }
}
package io.github.yinjinlong.h_nas.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import io.github.yinjinlong.h_nas.MainActivity
import io.github.yinjinlong.h_nas.Notifications
import io.github.yinjinlong.h_nas.R
import io.github.yinjinlong.h_nas.plugin.NotificationsPlugin
import io.github.yinjinlong.h_nas.utils.isDark

class MusicControlService : Service() {

    companion object {

        const val ID = 1

        const val ACTION_UPDATE = "update"
        const val ACTION_PLAY_PAUSE = "play_pause"
        const val ACTION_CLOSE = "close"

        const val EXTRA_TITLE = "title"
        const val EXTRA_ARTIST = "artist"
        const val EXTRA_PLAYING = "playing"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "?"
                val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "?"
                val playing = intent.getBooleanExtra(EXTRA_PLAYING, false)
                postNotification(title, artist, playing)
            }

            ACTION_PLAY_PAUSE -> {
                NotificationsPlugin.instance?.onPlayPause()
            }

            ACTION_CLOSE -> {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        NotificationsPlugin.instance?.onClose()
        super.onDestroy()
    }

    private fun postNotification(title: String, subText: String, playing: Boolean) {
        if (!notificationManager.areNotificationsEnabled())
            return
        val view = RemoteViews(applicationContext.packageName, R.layout.music_layout)

        view.setTextViewText(R.id.music_title, title)
        view.setTextViewText(R.id.music_artists, subText)


        val color = (if (applicationContext.isDark) 0xfffafafa else 0xff212121).toInt()

        view.setImageViewIcon(
            R.id.music_play_pause,
            Icon.createWithResource(
                applicationContext,
                if (playing) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24
            ).apply {
                setTint(color)
            }
        )
        view.setImageViewIcon(
            R.id.music_close,
            Icon.createWithResource(applicationContext, R.drawable.round_close_24).apply {
                setTint(color)
            })
        view.setOnClickPendingIntent(
            R.id.music_play_pause,
            PendingIntent.getForegroundService(
                applicationContext,
                0,
                Intent(this, MusicControlService::class.java).apply {
                    action = ACTION_PLAY_PAUSE
                },
                PendingIntent.FLAG_MUTABLE
            )
        )
        view.setOnClickPendingIntent(
            R.id.music_close,
            PendingIntent.getForegroundService(
                applicationContext,
                0,
                Intent(this, MusicControlService::class.java).apply {
                    action = ACTION_CLOSE
                },
                PendingIntent.FLAG_MUTABLE
            )
        )

        val notification = NotificationCompat.Builder(this, Notifications.CHANNEL_MUSIC_PLAYER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setCustomContentView(view)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_MUTABLE
                )
            )
            .build()

        startForeground(ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
    }
}
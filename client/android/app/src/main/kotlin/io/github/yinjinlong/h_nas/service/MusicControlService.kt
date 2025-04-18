package io.github.yinjinlong.h_nas.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import io.github.yinjinlong.h_nas.MainActivity
import io.github.yinjinlong.h_nas.Notifications
import io.github.yinjinlong.h_nas.R
import io.github.yinjinlong.h_nas.plugin.NotificationsPlugin
import io.github.yinjinlong.h_nas.utils.isDark
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import kotlin.math.min
import kotlin.math.roundToInt

class MusicControlService : Service() {

    companion object {

        const val ID = 1

        const val ACTION_UPDATE = "update"
        const val ACTION_PREVIOUS = "previous"
        const val ACTION_PLAY_PAUSE = "play_pause"
        const val ACTION_NEXT = "next"
        const val ACTION_CLOSE = "close"

        const val EXTRA_TITLE = "title"
        const val EXTRA_ARTIST = "artist"
        const val EXTRA_COVER = "cover"
        const val EXTRA_PLAYING = "playing"
    }

    private lateinit var notificationManager: NotificationManager
    private var coverBitmap: Bitmap? = null
    private var url: String? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "?"
                val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "?"
                val cover = intent.getStringExtra(EXTRA_COVER)
                val playing = intent.getBooleanExtra(EXTRA_PLAYING, false)
                postNotification(title, artist, cover, playing)
            }

            ACTION_PREVIOUS -> NotificationsPlugin.instance?.onPrevious()

            ACTION_PLAY_PAUSE -> NotificationsPlugin.instance?.onPlayPause()

            ACTION_NEXT -> NotificationsPlugin.instance?.onNext()

            ACTION_CLOSE -> stopSelf()

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

    private fun icon(@DrawableRes id: Int, color: Int) = Icon.createWithResource(applicationContext, id).apply {
        setTint(color)
    }

    private fun pendingIntent(action: String) = PendingIntent.getForegroundService(
        applicationContext,
        0,
        Intent(this@MusicControlService, MusicControlService::class.java).apply {
            this.action = action
        },
        PendingIntent.FLAG_MUTABLE
    )

    private fun RemoteViews.init(
        title: String,
        artists: String,
        color: Int, playing: Boolean
    ) {
        setTextViewText(R.id.music_title, title)
        setTextViewText(R.id.music_artists, artists)

        setImageViewIcon(
            R.id.music_play_pause,
            icon(if (playing) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24, color)
        )
        setImageViewIcon(
            R.id.music_close,
            icon(R.drawable.round_close_24, color)
        )

        setOnClickPendingIntent(
            R.id.music_play_pause,
            pendingIntent(ACTION_PLAY_PAUSE)
        )
        setOnClickPendingIntent(
            R.id.music_close,
            pendingIntent(ACTION_CLOSE)
        )
    }

    fun getCoverPix() = (resources.displayMetrics.density * 60).roundToInt()
    fun getCoverCornerPix() = resources.displayMetrics.density * 12

    @OptIn(DelicateCoroutinesApi::class)
    private fun postNotification(title: String, artists: String, cover: String?, playing: Boolean) {
        if (cover == null) {
            postNotification(title, artists, null as Bitmap?, playing)
            return
        }

        if (cover == url) {
            postNotification(title, artists, coverBitmap, playing)
        } else {
            url = cover
            postNotification(title, artists, null as Bitmap?, playing)
            GlobalScope.launch {
                val url = URI.create(cover).toURL()
                val conn = url.openConnection()
                runCatching {
                    conn.connect()
                    val size = getCoverPix()
                    val drawable = RoundedBitmapDrawableFactory.create(resources, conn.getInputStream()).apply {
                        paint.isAntiAlias = true
                        cornerRadius = getCoverCornerPix()
                    }
                    coverBitmap = Bitmap.createBitmap(
                        min(size, drawable.intrinsicWidth),
                        min(size, drawable.intrinsicHeight),
                        Bitmap.Config.ARGB_8888
                    ).applyCanvas {
                        drawable.bounds = Rect(0, 0, size, size)
                        drawable.draw(this)
                    }
                    postNotification(title, artists, coverBitmap, playing)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    private fun postNotification(title: String, artists: String, cover: Bitmap?, playing: Boolean) {
        if (!notificationManager.areNotificationsEnabled())
            return
        val color = (if (applicationContext.isDark) 0xfffafafa else 0xff212121).toInt()
        val view = RemoteViews(applicationContext.packageName, R.layout.music_layout)
        val viewBig = RemoteViews(applicationContext.packageName, R.layout.music_layout_big)
        view.init(title, artists, color, playing)

        viewBig.init(title, artists, color, playing)
        cover?.let { viewBig.setImageViewBitmap(R.id.music_cover, it) }
        viewBig.setOnClickPendingIntent(
            R.id.music_previous,
            pendingIntent(ACTION_PREVIOUS)
        )
        viewBig.setOnClickPendingIntent(
            R.id.music_next,
            pendingIntent(ACTION_NEXT)
        )

        viewBig.setImageViewIcon(R.id.music_previous, icon(R.drawable.round_skip_previous_24, color))
        viewBig.setImageViewIcon(R.id.music_next, icon(R.drawable.round_skip_next_24, color))

        val notification = NotificationCompat.Builder(this, Notifications.CHANNEL_MUSIC_PLAYER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(view)
            .setCustomBigContentView(viewBig)
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
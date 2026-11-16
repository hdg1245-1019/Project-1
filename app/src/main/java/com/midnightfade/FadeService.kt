package com.midnightfade

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import java.time.LocalTime

class FadeService : Service() {
    private val channelId = "midnight_fade"
    private val handler = Handler(Looper.getMainLooper())
    private var overlay: View? = null
    private var manager: WindowManager? = null

    private val tick = object : Runnable {
        override fun run() {
            if (!Settings.canDrawOverlays(this@FadeService)) {
                stopSelf()
                return
            }
            val now = LocalTime.now()
            val minute = now.hour * 60 + now.minute
            val elapsed = if (minute < 360) minute else -1
            val alpha = if (elapsed >= 0) (elapsed / 45f).coerceIn(0f, 1f) * 0.32f else 0f
            overlay?.alpha = alpha
            handler.postDelayed(this, 15_000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, "Midnight Fade", NotificationManager.IMPORTANCE_LOW)
        )
        val stopIntent = PendingIntent.getService(
            this, 1, Intent(this, FadeService::class.java).setAction("STOP"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Midnight Fade active")
            .setContentText("Warm screen tint runs after midnight")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopIntent)
            .build()
        startForeground(1, notification)
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        manager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlay = View(this).apply {
            setBackgroundColor(Color.rgb(255, 150, 55))
            alpha = 0f
            isClickable = false
            isFocusable = false
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP }
        manager?.addView(overlay, params)
        handler.post(tick)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        overlay?.let { manager?.removeView(it) }
        overlay = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

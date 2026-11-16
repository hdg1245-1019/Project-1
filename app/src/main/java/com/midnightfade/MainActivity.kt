package com.midnightfade

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 64, 40, 40)
        }
        layout.addView(TextView(this).apply {
            text = "Midnight Fade\nAfter midnight, a warm overlay gradually appears. You can stop it any time."
            textSize = 20f
        })
        layout.addView(Button(this).apply {
            text = "Start overnight overlay"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                } else {
                    startForegroundService(Intent(this@MainActivity, FadeService::class.java))
                }
            }
        })
        layout.addView(Button(this).apply {
            text = "Stop overlay"
            setOnClickListener { stopService(Intent(this@MainActivity, FadeService::class.java)) }
        })
        setContentView(layout)
    }
}

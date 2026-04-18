package com.autohub.launcher.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.autohub.launcher.R
import com.autohub.launcher.AutoHubApplication
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FloatingBallService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingBallView: View? = null
    private var floatingBallParams: WindowManager.LayoutParams? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // 先启动前台服务通知
        startForegroundServiceNotification()
        
        // 检查悬浮窗权限后再创建悬浮球
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            createFloatingBall()
        }
    }

    private fun createFloatingBall() {
        try {
            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            floatingBallView = layoutInflater.inflate(R.layout.floating_ball, null)

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.TOP or Gravity.END
            params.x = 100
            params.y = 200

            floatingBallParams = params
            windowManager.addView(floatingBallView, params)

            setupTouchListener()
        } catch (e: Exception) {
            // 权限不足或其他错误，停止服务
            floatingBallView = null
            stopSelf()
        }
    }

    private fun setupTouchListener() {
        floatingBallView?.setOnTouchListener { view, event ->
            val params = floatingBallParams ?: return@setOnTouchListener false
            
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingBallView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val screenWidth = resources.displayMetrics.widthPixels
                    val viewHalfWidth = floatingBallView?.width?.div(2) ?: 0

                    // Snap to edge
                    if (params.x < screenWidth / 2) {
                        params.x = 0
                    } else {
                        params.x = screenWidth - viewHalfWidth * 2
                    }

                    // Check if it was a click (minimal movement)
                    val deltaX = Math.abs(event.rawX - initialTouchX)
                    val deltaY = Math.abs(event.rawY - initialTouchY)

                    if (deltaX < 10 && deltaY < 10) {
                        // It was a click
                        handleBallClick()
                    } else {
                        windowManager.updateViewLayout(floatingBallView, params)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun handleBallClick() {
        // Show quick actions menu
        // TODO: Implement quick actions menu
    }

    private fun startForegroundServiceNotification() {
        val notification = NotificationCompat.Builder(this, AutoHubApplication.NOTIFICATION_CHANNEL_ID_SERVICE)
            .setContentTitle("AutoHub 悬浮球")
            .setContentText("悬浮球正在运行")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            floatingBallView?.let {
                windowManager.removeView(it)
            }
        } catch (e: Exception) {
            // View already removed
        }
        floatingBallView = null
    }

    companion object {
        fun startService(context: Context) {
            val intent = Intent(context, FloatingBallService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FloatingBallService::class.java)
            context.stopService(intent)
        }
    }
}

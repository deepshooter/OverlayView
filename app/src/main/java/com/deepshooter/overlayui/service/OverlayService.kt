package com.deepshooter.overlayui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import com.deepshooter.overlayui.view.OverlayView

/**
 * OverlayService is a foreground/background service that creates
 * and displays a floating overlay view on the screen.
 */
class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var layoutParams: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Initialize WindowManager to control system overlay windows
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Create and initialize the overlay view using the updated OverlayView
        val overlayViewInitializer = OverlayView(context = this, service = this)
        val (view, params) = overlayViewInitializer.createViewAndParams()

        overlayView = view
        layoutParams = params

        // Add the overlay view to the window
        windowManager.addView(overlayView, layoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Clean up the view from the window to prevent leaks
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}

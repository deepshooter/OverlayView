package com.deepshooter.overlayui.view

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import com.deepshooter.overlayui.R

/**
 * OverlayView is responsible for creating, configuring, and handling
 * the behavior of a floating overlay view in an Android application.
 *
 * It supports:
 * - Inflating the overlay layout
 * - Configuring WindowManager.LayoutParams
 * - Enabling dragging functionality
 * - Handling close button to stop the service
 *
 * @param context The context used to inflate the view and access system services.
 * @param service The service that manages the overlay and will be stopped on close.
 */
class OverlayView(
    private val context: Context,
    private val service: Service
) {

    // The inflated overlay view
    private lateinit var overlayView: View

    // Layout parameters used to control the overlay position and behavior
    private lateinit var layoutParams: WindowManager.LayoutParams

    /**
     * Creates the overlay view and its associated layout parameters.
     * Also binds the close button and enables dragging.
     *
     * @return Pair of the view and its layout parameters
     */
    fun createViewAndParams(): Pair<View, WindowManager.LayoutParams> {
        // Inflate the custom overlay layout
        overlayView = LayoutInflater.from(context)
            .inflate(R.layout.layout_overlay_view, FrameLayout(context), false)


        // Create layout parameters for the overlay
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER or Gravity.START
            x = 100
            y = 100
        }

        // Hook up close button and dragging behavior
        bindCloseButton(R.id.btn_close_overlay)
        enableDragging()

        return overlayView to layoutParams
    }

    /**
     * Binds a close button within the overlay view that stops the service when clicked.
     */
    private fun bindCloseButton(buttonId: Int) {
        overlayView.findViewById<ImageButton>(buttonId)?.setOnClickListener {
            service.stopSelf()
        }
    }

    /**
     * Enables dragging for the overlay view by handling touch events.
     */
    private fun enableDragging() {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        overlayView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    true
                }

                else -> false
            }
        }
    }
}

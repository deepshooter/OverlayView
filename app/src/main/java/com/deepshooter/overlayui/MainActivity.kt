package com.deepshooter.overlayui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.deepshooter.overlayui.service.OverlayService

class MainActivity : AppCompatActivity() {

    // ActivityResultLauncher for managing the overlay permission request
    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handleOverlayPermissionResult()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup button to start the overlay
        setupStartOverlayButton()
    }

    // Setup the button click listener to start the overlay service
    private fun setupStartOverlayButton() {
        val startOverlayButton: Button = findViewById(R.id.startOverlayButton)
        startOverlayButton.setOnClickListener {
            if (hasOverlayPermission()) {
                startOverlayService()
            } else {
                requestOverlayPermission()
            }
        }
    }

    // Check if the app has permission to show overlays
    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    // Request permission to show overlays from the settings screen
    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        overlayPermissionLauncher.launch(intent)
    }

    // Handle the result after the user grants or denies the overlay permission
    private fun handleOverlayPermissionResult() {
        if (hasOverlayPermission()) {
            startOverlayService()
        } else {
            showToast("Permission not granted!")
        }
    }

    // Start the overlay service if permission is granted
    private fun startOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        startService(intent)
        showToast("Overlay started!")
        finishAndRemoveTask()  // Close the app after starting the service
    }

    // Utility function to show toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

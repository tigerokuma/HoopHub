package com.example.hoophubskeleton

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher

class ProfileImageLauncher(
    // Gets permissions, launches intent based on if camera or gallery are selected
    // Camera permissions adapted from camera lab shown in class
    private val context: Context,
    private var snappedPhotoLauncher: ActivityResultLauncher<Intent>,
    private var galleryPhotoLauncher: ActivityResultLauncher<Intent>,
    private var tempPhotoUri: Uri?
) {
    fun launch() {
        val customView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
        val title = customView.findViewById<TextView>(R.id.customTitle)
        val message = customView.findViewById<TextView>(R.id.customMessage)
        val galleryButton = customView.findViewById<Button>(R.id.cancelButton)
        val cameraButton = customView.findViewById<Button>(R.id.confirmButton)

        title.text = "Change Profile Picture"
        message.text =
            "Select image source:"
        galleryButton.text = "Gallery"
        cameraButton.text = "Camera"


        val dialog = AlertDialog.Builder(context)
            .setView(customView)
            .create()

        // Set up button actions
        galleryButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // launches gallery
            galleryPhotoLauncher.launch(galleryIntent)
            dialog.dismiss()
            dialog.dismiss() // Close dialog when "Cancel" is clicked
        }

        cameraButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // sets URI to store snapped image
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri)
            // launches camera
            snappedPhotoLauncher.launch(cameraIntent)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

}


package com.example.hoophubskeleton

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
/*
The following code for taking and storing a photo was adapted from the worked-through
code shown in the September 13th, 2024 lecture.

External permissions adapted from
Manage External Storage Permission -Android Studio — Java
https://medium.com/@kezzieleo/manage-external-storage-permission-android-studio-java-9c3554cf79a7
*/
object Util {

    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return

        // asks user for permission for the camera and gallery together
        // NOTE TO TA: this is JANKY, like the provided apk file.
        // it is assumed the user will select "Allow" to both cases
        // otherwise the app will crash
        // The reason for this is that the apk MyRuns-Android-chk2.apk
        // will crash if not given permissions, and there is no edge case to deal with it.
        // I am implementing the functionality corresponding exactly as the provided apk

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES), 0)
        }


    }


    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }
}
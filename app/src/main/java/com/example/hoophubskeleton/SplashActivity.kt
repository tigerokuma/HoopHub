package com.example.hoophubskeleton

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize the MediaPlayer with the splash sound
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_sound)

        // Start playing the sound
        mediaPlayer.start()

        // Transition to the next activity after a fixed delay (e.g., 3 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextActivity()
        }, 3000) // hold for 2 sec
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this, AuthHostActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer when the activity is destroyed
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}

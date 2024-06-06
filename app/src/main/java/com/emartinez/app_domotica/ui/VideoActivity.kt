package com.emartinez.app_domotica.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emartinez.app_domotica.databinding.ActivityVideoBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowInsetsController

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoLayout: VLCVideoLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Ocultar la barra de navegación
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    // Ocultar la barra de estado
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    // Mantener el contenido de la aplicación por debajo de las barras
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Mantener la barra de navegación oculta cuando se interactúa
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        val options = ArrayList<String>()
        libVLC = LibVLC(this, options)
        mediaPlayer = MediaPlayer(libVLC)
        videoLayout = binding.vlcVideoLayout

        videoLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (videoLayout.measuredWidth > 0 && videoLayout.measuredHeight > 0) {
                    mediaPlayer.attachViews(videoLayout, null, true, true)
                    videoLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        val streamUrl = intent.getStringExtra("streamUrl")
        if (streamUrl != null) {
            val media = Media(libVLC, Uri.parse(streamUrl))
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        libVLC.release()
    }
}
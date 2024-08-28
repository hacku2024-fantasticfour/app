package com.example.huck_app

import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class VideoMode : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_mode)


        player = ExoPlayer.Builder(this).build()

        surfaceView = findViewById(R.id.video_view)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                player.setVideoSurface(holder.surface)
                startRtspStream()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                player.release()
            }
        })
    }

    private fun startRtspStream() {
        val rtspUri = Uri.parse("rtsp://username:password@your_rtsp_stream_url")

        val mediaItem = MediaItem.fromUri(rtspUri)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

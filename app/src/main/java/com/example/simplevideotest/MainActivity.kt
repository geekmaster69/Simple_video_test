package com.example.simplevideotest

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.simplevideotest.databinding.ActivityMainBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity(){
    //private var url: String = "rtsp://201.155.194.137:9090/dss/monitor/params?cameraid=1000085%240&substream=1"
    //private var url: Uri = "https://joy1.videvo.net/videvo_files/video/free/video0467/large_watermarked/_import_61516586ee8571.11252072_preview.mp4".toUri()

    private lateinit var constrainLayoutRoot: ConstraintLayout
    private lateinit var exoPlayerView: StyledPlayerView
    private lateinit var simpleExoPlayer: ExoPlayer
    private lateinit var mediaSource: MediaSource
    private lateinit var urlType: URLType
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        findView()
        initPlayer()


    }
    private fun findView(){
        constrainLayoutRoot = binding.constraintLayoutRoot
        exoPlayerView = binding.exoPlayerView
    }

    private fun initPlayer(){
        simpleExoPlayer = ExoPlayer.Builder(this).build()


        simpleExoPlayer.addListener(playListener)

        exoPlayerView.player = simpleExoPlayer

        //Create media source

        createMediaSource()



        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()

    }

    private fun createMediaSource(){
        urlType = URLType.MP4
        urlType.url = "https://joy1.videvo.net/videvo_files/video/free/2014-05/large_watermarked/Futuristic_Numbers_Countdown__Videvo_preview.mp4"

//        urlType = URLType.HLS
//        urlType.url = "rtsp://201.155.194.137:9090/dss/monitor/params?cameraid=1000072%240&substream=1"

        simpleExoPlayer.seekTo(0)

        when(urlType){
            URLType.MP4 ->{

                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    this,
                    Util.getUserAgent(this, applicationInfo.name)
                )
                mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(urlType.url))
                )
            }
            URLType.HLS ->{

                 mediaSource = RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(Uri.parse(urlType.url)))

            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val constrainSet = ConstraintSet()
        constrainSet.connect(exoPlayerView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constrainSet.connect(exoPlayerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        constrainSet.connect(exoPlayerView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        constrainSet.connect(exoPlayerView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

        constrainSet.applyTo(constrainLayoutRoot)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            hideSystemUi()
        }else{
            showSystemUi()
            val layoutParams = exoPlayerView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = "16:9"
        }
    }

    private fun hideSystemUi(){
        actionBar?.hide()
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    private fun showSystemUi(){
        actionBar?.show()

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    override fun onResume() {
        super.onResume()

        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
        simpleExoPlayer.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer.pause()
        simpleExoPlayer.playWhenReady = false
    }


    override fun onDestroy() {
        super.onDestroy()

        simpleExoPlayer.removeListener(playListener)
        simpleExoPlayer.stop()
        simpleExoPlayer.clearMediaItems()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private var playListener = object : Player.Listener{
        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            if (urlType == URLType.HLS){
                exoPlayerView.useController = false
            }

            if (urlType == URLType.MP4){
                exoPlayerView.useController = true
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(this@MainActivity, "${error.message}", Toast.LENGTH_SHORT ).show()
        }
    }
}

enum class URLType(var url: String){
    MP4(""), HLS("")
}
package com.example.simplevideotest

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.example.simplevideotest.databinding.ActivityMainBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var libVlc: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoLayout: VLCVideoLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = Uri.parse("rtsp://201.155.194.137:9090/dss/monitor/params?cameraid=1000072%242&substream=1")

        libVlc = LibVLC(this)
        mediaPlayer = MediaPlayer(libVlc)
        videoLayout = binding.videoView

        mediaPlayer.attachViews(videoLayout, null, false, false)

        val media = Media(libVlc, uri)
        media.setHWDecoderEnabled(true, false)
        media.addOption(":network-caching=600")

        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()










    }


}
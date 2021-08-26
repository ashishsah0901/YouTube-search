package com.example.youtubesearch.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.youtubesearch.databinding.ActivityYouTubePlayerBinding
import com.example.youtubesearch.util.Constants.API_KEY
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.coroutines.launch

class YouTubePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYouTubePlayerBinding
    private var videoId: String? = null
    private var currentSec: Float = 0F
    private var title: String? = null
    private var description: String? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYouTubePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoId = intent.getStringExtra("videoId")
        title = intent.getStringExtra("title")
        description = intent.getStringExtra("description")
        date = intent.getStringExtra("date")
        currentSec = 0F
        lifecycle.addObserver(binding.fragmentYoutubePlayer)
        binding.fragmentYoutubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                super.onReady(youTubePlayer)
                youTubePlayer.cueVideo(videoId!!, currentSec)
            }

            override fun onError(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                Toast.makeText(this@YouTubePlayerActivity, error.name, Toast.LENGTH_SHORT).show()
            }

            override fun onCurrentSecond(
                youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer,
                second: Float
            ) {
                super.onCurrentSecond(youTubePlayer, second)
                currentSec = second
            }

        })

        binding.fragmentYoutubePlayer.addFullScreenListener(object : YouTubePlayerFullScreenListener{
            override fun onYouTubePlayerEnterFullScreen() {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                supportActionBar?.hide()
                hideSystemUI()
                binding.fragmentYoutubePlayer.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                supportActionBar?.show()
                showSystemUI()
                binding.fragmentYoutubePlayer.exitFullScreen()
            }

        })
//        binding.videoTitleTextView.text = title
//        binding.youtubeVideoDetails.text = description
//        binding.uploadDateTv.text = date!!.substring(0, 10) + " " + date!!.substring(11, 16)
        binding.shareVideoButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=$videoId")
                type = "text/plain"
                startActivity(this)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            binding.fragmentYoutubePlayer.exitFullScreen()
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            supportActionBar?.show()
            showSystemUI()
        }
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            supportActionBar?.hide()
            hideSystemUI()
            binding.fragmentYoutubePlayer.enterFullScreen()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, binding.root).show(WindowInsetsCompat.Type.systemBars())
    }

}
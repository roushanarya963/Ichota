package com.ichota.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.ichota.R
import com.ichota.databinding.ActivitySplashBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper


class SplashActivity : AppCompatActivity() {
    private val delayTime = 7000L //ms
    private lateinit var binding: ActivitySplashBinding
    private var isUserLogIn = false
    private var mCurrentPlayPosition: Int = 0
    private var mPlayer: SimpleExoPlayer? = null

    private var playWhenReady: Boolean = true
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        isUserLogIn = PreferenceHelper.getPreferences(this).getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val runnable = Runnable {
            if (isUserLogIn) moveTo(MainActivity())
            else moveTo(OnBoarding1())
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,4000)

     /*   val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)
       // val uri = Uri.parse("https://web.law.duke.edu/cspd/contest/videos/Framed-Contest_Documentaries-and-You.mp4")
        binding.videoView.setVideoURI(uri)
        binding.videoView.setOnCompletionListener {
            Log.d("TAG", "onCreate: ${it.isPlaying}")
            if (!it.isPlaying) {
                if (isUserLogIn) moveTo(MainActivity())
                else moveTo(OnBoarding1())
            }
        }

        binding.videoView.setOnInfoListener { mediaPlayer, i, i2 ->
            if(i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                binding.viewForeground.visibility = View.GONE
                return@setOnInfoListener true

            }
            return@setOnInfoListener false*/

      //  }

        //  Handler().postDelayed(runnable, delayTime)
        // binding.tvName.setCharacterDelay(110)
        // binding.tvName.animateText("CHOTA")

    }

  /* private fun initializePlayer() {
        mPlayer = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = mPlayer
        val uri = Uri.parse("https://web.law.duke.edu/cspd/contest/videos/Framed-Contest_Documentaries-and-You.mp4")
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        mPlayer?.setMediaItem(mediaItem)
        mPlayer?.playWhenReady = playWhenReady
        mPlayer?.seekTo(currentWindow, playbackPosition)
        mPlayer?.prepare()
    }*/

    override fun onStart() {
        super.onStart()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initializePlayer()
        }*/

         binding.videoView.start()
    }


    override fun onResume() {
        super.onResume()

       // hideSystemUi()
       /* if (Build.VERSION.SDK_INT < 24 || mPlayer == null) {
            initializePlayer()
        }*/

         if (!binding.videoView.isPlaying) {
             binding.videoView.seekTo(mCurrentPlayPosition)
             binding.videoView.resume()
         }


    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    override fun onPause() {
         if (binding.videoView.isPlaying) {
             mCurrentPlayPosition = binding.videoView.currentPosition
             binding.videoView.pause()
         }

        super.onPause()

       /* if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }*/


    }

    override fun onStop() {
        binding.videoView.stopPlayback()
        super.onStop()

       /* if (Build.VERSION.SDK_INT >=24){
            releasePlayer()

        }*/


    }


    private fun releasePlayer() {

        if (mPlayer != null) {
            playWhenReady = mPlayer!!.getPlayWhenReady()
            playbackPosition = mPlayer!!.getCurrentPosition()
            currentWindow = mPlayer!!.getCurrentWindowIndex()
            mPlayer!!.release()
            mPlayer = null
        }

    }


    private fun moveTo(activity: AppCompatActivity) {
        Intent(this, activity::class.java).also {
            startActivity(it)
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }
}
package com.fatihbayrakci.memory_game

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.fatihbayrakci.memory_game.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var firstFlippedImageView: ImageView? = null
    private var secondFlippedImageView: ImageView? = null
    private var matchedPairs = 0
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioManager: AudioManager
    private lateinit var volumeSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this, R.raw.back)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        volumeSeekBar = findViewById(R.id.volume_seek_baar)
        volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        setListeners()

        val restartBtn = findViewById<ImageButton>(R.id.btnRestart)
        restartBtn.setOnClickListener {
            recreate()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun setListeners() {
        val clickableViewIds: List<Int> = listOf(
            R.id.image_box_1,
            R.id.image_box_2,
            R.id.image_box_3,
            R.id.image_box_4,
            R.id.image_box_5,
            R.id.image_box_6,
            R.id.image_box_7,
            R.id.image_box_8,
            R.id.image_box_9,
            R.id.image_box_10,
            R.id.image_box_11,
            R.id.image_box_12
        )

        for (itemId in clickableViewIds) {
            findViewById<ImageView>(itemId).setOnClickListener { flipCard(it as ImageView) }
        }
    }

    private fun flipCard(imageView: ImageView) {
        if (firstFlippedImageView == null) {
            firstFlippedImageView = imageView
            imageView.setImageResource(getImageResource(imageView))
        } else if (firstFlippedImageView != imageView && secondFlippedImageView == null) {
            secondFlippedImageView = imageView
            imageView.setImageResource(getImageResource(imageView))

            if (isMatch(firstFlippedImageView!!, secondFlippedImageView!!)) {
                matchedPairs++

                val scaleRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_anim)

                firstFlippedImageView?.startAnimation(scaleRotateAnimation)
                secondFlippedImageView?.startAnimation(scaleRotateAnimation)
                playOpeningSound()
                scaleRotateAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        firstFlippedImageView?.visibility = View.INVISIBLE
                        secondFlippedImageView?.visibility = View.INVISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            firstFlippedImageView?.visibility = View.INVISIBLE
                            secondFlippedImageView?.visibility = View.INVISIBLE

                            firstFlippedImageView?.setImageResource(R.drawable.square)
                            secondFlippedImageView?.setImageResource(R.drawable.square)

                            firstFlippedImageView = null
                            secondFlippedImageView = null
                        }, 200)

                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                if (matchedPairs == 6) {
                    val restartBtn = findViewById<ImageButton>(R.id.btnRestart)
                    val congImageView = findViewById<ImageView>(R.id.congratsScreen)
                    restartBtn.visibility = View.VISIBLE
                    congImageView.visibility = View.VISIBLE
                }

            }

            else {
                failureCard()
                Handler(Looper.getMainLooper()).postDelayed({
                    firstFlippedImageView?.setImageResource(R.drawable.square)
                    secondFlippedImageView?.setImageResource(R.drawable.square)
                    firstFlippedImageView = null
                    secondFlippedImageView = null
                }, 1000)
            }
        }
    }

    private fun getImageResource(imageView: ImageView): Int {
        return when (imageView.id) {
            R.id.image_box_1 -> R.drawable.image
            R.id.image_box_2 -> R.drawable.image2
            R.id.image_box_3 -> R.drawable.image3
            R.id.image_box_4 -> R.drawable.image4
            R.id.image_box_5 -> R.drawable.image5
            R.id.image_box_6 -> R.drawable.image6
            R.id.image_box_7 -> R.drawable.image3
            R.id.image_box_8 -> R.drawable.image
            R.id.image_box_9 -> R.drawable.image4
            R.id.image_box_10 -> R.drawable.image5
            R.id.image_box_11 -> R.drawable.image6
            R.id.image_box_12 -> R.drawable.image2
            else -> R.drawable.square
        }
    }

    private fun playOpeningSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.correct_card)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    private fun failureCard() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.wrong_card)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    private fun isMatch(imageView1: ImageView, imageView2: ImageView): Boolean {
        val resourceId1 = getImageResource(imageView1)
        val resourceId2 = getImageResource(imageView2)
        return resourceId1 == resourceId2
    }
}
















    /*private fun makeColored(view: ImageView) {
        val drawableResId = when (view.id) {
            R.id.image_box_1 -> R.drawable.image
            R.id.image_box_2 -> R.drawable.image2
            R.id.image_box_3 -> R.drawable.image3
            R.id.image_box_4 -> R.drawable.image4
            R.id.image_box_5 -> R.drawable.image5
            R.id.image_box_6 -> R.drawable.image6
            R.id.image_box_7 -> R.drawable.image3
            R.id.image_box_8 -> R.drawable.image
            R.id.image_box_9 -> R.drawable.image4
            R.id.image_box_10 -> R.drawable.image5
            R.id.image_box_11 -> R.drawable.image6
            R.id.image_box_12 -> R.drawable.image2

            // Diğer resimler için de durumları ekleyin
            else -> R.drawable.ic_launcher_foreground
        }
        view.setImageResource(drawableResId)





    }*/

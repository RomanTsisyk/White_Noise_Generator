package com.roman_tsisyk.whitenoisegenerator

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

class WhiteNoiseGenerator(context: Context, private val durationSeconds: Int) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val originalMode = audioManager.mode
    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false

    fun start() {
        // Reduce the volume to 50% to save battery
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        val targetVolume = maxVolume / 2
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, targetVolume, 0)

        // Set the audio mode to MODE_IN_COMMUNICATION for better power efficiency
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        // Start playing the white noise through the speaker
        audioManager.isSpeakerphoneOn = true
        val toneGenerator = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100)
        isPlaying = true
        val toneRunnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_PRESSHOLDKEY_LITE, 200)
                    handler.postDelayed(this, 800)
                }
            }
        }
        handler.post(toneRunnable)

        // Stop playing the white noise after the specified duration
        handler.postDelayed({
            isPlaying = false
            toneGenerator.release()
            audioManager.isSpeakerphoneOn = false
            audioManager.mode = originalMode
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, originalVolume, 0)
        }, durationSeconds * 1000L)
    }

    fun stop() {
        isPlaying = false
    }

    companion object {
        private const val originalVolume = 0
    }
}

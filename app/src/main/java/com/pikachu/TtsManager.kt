package com.pikachu

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * TtsManager – on-device TextToSpeech wrapper configured for Bengali voice selection.
 * It exposes simple speak(text) and setRole(role) APIs where role can be "male" or "female".
 */
class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val bn = Locale("bn")
            val res = tts?.setLanguage(bn)
            ready = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
            Log.d("TtsManager", "TTS init ready=$ready")
        } else {
            Log.e("TtsManager", "TTS init failed: $status")
        }
    }

    fun setRole(role: String) {
        if (!ready) return
        try {
            val voices = tts?.voices ?: return
            // Try to pick a voice by name heuristics (device dependent)
            val candidate = when (role.lowercase()) {
                "male" -> voices.firstOrNull { it.name.contains("male", true) || it.name.contains("m", true) }
                "female" -> voices.firstOrNull { it.name.contains("female", true) || it.name.contains("f", true) }
                else -> null
            }
            candidate?.let { tts?.voice = it }
        } catch (e: Exception) {
            Log.e("TtsManager", "setRole error: ${e.message}")
        }
    }

    fun speak(text: String) {
        if (!ready) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PikachuUtterance")
    }

    fun shutdown() {
        tts?.shutdown()
    }
}

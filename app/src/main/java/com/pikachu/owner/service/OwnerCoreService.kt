package com.pikachu.owner.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class OwnerCoreService : AccessibilityService(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private var speechRecognizer: SpeechRecognizer? = null
    private var isMasterSwitchActive: Boolean = false

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        setupSpeechRecognizer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isMasterSwitchActive = intent?.getBooleanExtra("MASTER_SWITCH", false) ?: false
        return START_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isMasterSwitchActive) return

        val packageName = event?.packageName?.toString()
        val textContent = event?.text?.toString()

        if (packageName != null && textContent != null) {
            analyzeScreenForTrading(packageName, textContent)
        }
    }

    override fun onInterrupt() {}

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale("bn", "BD"))
            textToSpeech.setPitch(1.2f)
            textToSpeech.setSpeechRate(0.95f)
        }
    }

    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {}
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        if (spokenText.contains("পিকাজু", ignoreCase = true)) {
                            speakOut("বলুন বস, আমি শুনছি।")
                        }
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun analyzeScreenForTrading(packageName: String, content: String) {
        // implement screen analysis
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        speechRecognizer?.destroy()
        super.onDestroy()
    }
}

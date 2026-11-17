package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SoundEngine(context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isSoundEnabled: Boolean = true

    @Volatile
    var isTtsEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            Log.e("SoundEngine", "Failed to initialize ToneGenerator", e)
        }

        try {
            val ttsContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                try {
                    context.createAttributionContext("SoundEngine")
                } catch (e: Exception) {
                    context.applicationContext
                }
            } else {
                context.applicationContext
            }
            tts = TextToSpeech(ttsContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsInitialized = true
                    try {
                        tts?.language = Locale("ar")
                    } catch (e: Exception) {
                        Log.e("SoundEngine", "Error setting language", e)
                    }
                } else {
                    Log.e("SoundEngine", "TTS Initialization failed!")
                }
            }
        } catch (e: Exception) {
            Log.e("SoundEngine", "TTS service unavailable", e)
        }
    }

    private val encouragements = listOf(
        "أحسنت يا بطل!",
        "ممتاز جداً!",
        "رائع، إجابة صحيحة!",
        "أنت ذكي جداً!",
        "مبدع ومذهل!",
        "أصبت الهدف!",
        "عمل رائع ومميز!"
    )

    private val englishEncouragements = listOf(
        "Super job, champion!",
        "Brilliant answer!",
        "You are amazing!",
        "Correct! Well done!",
        "Awesome work!",
        "Keep shining, star!"
    )

    fun playCorrectSound(isEnglish: Boolean = false) {
        coroutineScope.launch {
            try {
                if (isSoundEnabled) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 120)
                    delay(150)
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 180)
                    delay(350)
                }
                if (isTtsEnabled && isTtsInitialized && tts != null) {
                    tts?.language = if (isEnglish) Locale.US else Locale("ar")
                    val phrase = if (isEnglish) englishEncouragements.random() else encouragements.random()
                    tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "EncouragementTTS")
                }
            } catch (e: Exception) {
                Log.e("SoundEngine", "Error playing correct sound", e)
            }
        }
    }

    fun playClickSound() {
        if (!isSoundEnabled) return
        coroutineScope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
            } catch (e: Exception) {
                Log.e("SoundEngine", "Error playing click sound", e)
            }
        }
    }

    fun playIncorrectSound() {
        if (!isSoundEnabled) return
        coroutineScope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 220)
            } catch (e: Exception) {
                Log.e("SoundEngine", "Error playing incorrect sound", e)
            }
        }
    }

    fun playCelebrationSound(isEnglish: Boolean = false) {
        coroutineScope.launch {
            try {
                if (isSoundEnabled) {
                    val tones = listOf(
                        ToneGenerator.TONE_CDMA_PIP,
                        ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
                        ToneGenerator.TONE_SUP_PIP
                    )
                    for (t in tones) {
                        toneGenerator?.startTone(t, 100)
                        delay(130)
                    }
                    delay(200)
                }
                if (isTtsEnabled && isTtsInitialized && tts != null) {
                    tts?.language = if (isEnglish) Locale.US else Locale("ar")
                    val message = if (isEnglish) {
                        "Super job! You completed the challenge like a champion!"
                    } else {
                        "رائع جداً! لقد أكملت التحدي بنجاح باهر يا بطل!"
                    }
                    tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "CelebrationTTS")
                }
            } catch (e: Exception) {
                Log.e("SoundEngine", "Error playing celebration", e)
            }
        }
    }

    fun speak(text: String, isEnglish: Boolean = false, force: Boolean = false) {
        if (!force && !isTtsEnabled) return
        if (!isTtsInitialized || tts == null) {
            Log.w("SoundEngine", "TTS is not ready or failed to load")
            return
        }
        try {
            tts?.language = if (isEnglish) Locale.US else Locale("ar")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "QuestionTTS")
        } catch (e: Exception) {
            Log.e("SoundEngine", "Speak failure", e)
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("SoundEngine", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            toneGenerator?.release()
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("SoundEngine", "Error shutting down SoundEngine", e)
        }
    }
}

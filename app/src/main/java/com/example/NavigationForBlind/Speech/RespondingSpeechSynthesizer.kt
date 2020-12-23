package com.example.NavigationForBlind.Speech

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.NavigationForBlind.SettingsUtils.Settings
import com.example.NavigationForBlind.SettingsUtils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RespondingSpeechSynthesizer  <E : Enum<E>>  (private val context: Context, private val message: String, private val returnCommand: E, val speak: (m: E) -> Unit) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(context, this, "com.google.android.tts")

    override fun onInit(i: Int)  {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (i == TextToSpeech.SUCCESS) {

            val locale = Utils.languageToLocale(Settings.language)
            val result: Int
            result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.wtf("Speech Synthesiser", "This Language is not supported")
            }
            else {
                speakOut(message)
            }

        } else {
            Log.wtf("Speech Synthesiser", "Init failed!")
        }
    }

    private fun speakOut(message: String)  = GlobalScope.launch  {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        while(tts.isSpeaking){}

        Handler(Looper.getMainLooper()).post {
            speak(returnCommand)
        }
    }
}
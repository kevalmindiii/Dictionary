package com.example.dictionary

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dictionary.DictionaryModel.DictionaryModelItem
import com.example.dictionary.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


private const val REQUEST_CODE_SPEECH_INPUT = 1

class MainActivity : AppCompatActivity() {


    var audioUrl = ""
    lateinit var binding: ActivityMainBinding
    // private val mediaPlayer = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivOption.setOnClickListener {

        }
        binding.ivMic.setOnClickListener {
            mic()
        }
        binding.speaker.setOnClickListener {
            playAudio()
        }
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchData = binding.etSearch.text.toString()
                if (searchData.isNotEmpty()) {
                    searchMessage(searchData)
                } else {
                    Toast.makeText(this, "Please enter any word", Toast.LENGTH_SHORT).show()
                }
            }
            handled
        }
    }

    private fun playAudio() {
        // if(mediaPlayer.isPlaying) return
        val audioUrll = "https:$audioUrl"
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer.setDataSource(audioUrll)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun searchMessage(name: String) {
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$name"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val type: Type = object : TypeToken<ArrayList<DictionaryModelItem>>() {
                }.type
                val gson: ArrayList<DictionaryModelItem> =
                    Gson().fromJson<ArrayList<DictionaryModelItem>>(
                        response,
                        type
                    )
                val builder = StringBuilder()
                builder.append(gson[0].word).append("\n").append(gson[0].phonetics[0].text)
                    .append("\n").append(gson[0].origin).append("\n")
                    .append(gson[0].meanings[0].definitions[0].definition)
                binding.tvWord.text = (gson[0].word)
                binding.tvPhonics.text = (gson[0].phonetic)
                binding.tvDefinition.text = (gson[0].meanings[0].definitions[0].definition)
                binding.linearforVisible.visibility = View.VISIBLE
                audioUrl = (gson[0].phonetics[0].audio)

            },
            { error ->
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            })
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun mic() {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        }.also {
            try {
                startActivityForResult(it, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast
                    .makeText(
                        this@MainActivity, e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                val str = Objects.requireNonNull(result)?.get(0)
                if (str != null) searchMessage(str)
                binding.etSearch.setText(
                    Objects.requireNonNull(result)?.get(0)
                )
                binding.etSearch.setSelection(binding.etSearch.text.toString().length)
            }
        }
    }
}
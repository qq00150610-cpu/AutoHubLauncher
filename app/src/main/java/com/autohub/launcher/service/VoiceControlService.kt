package com.autohub.launcher.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class VoiceControlService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private val _recognitionState = MutableStateFlow(VoiceRecognitionState())
    val recognitionState: StateFlow<VoiceRecognitionState> = _recognitionState.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        setupRecognitionListener()
    }

    private fun setupRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _recognitionState.value = _recognitionState.value.copy(
                    isListening = true,
                    status = "准备就绪，请说话..."
                )
            }

            override fun onBeginningOfSpeech() {
                _recognitionState.value = _recognitionState.value.copy(
                    isListening = true,
                    status = "正在聆听..."
                )
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Update audio level indicator
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _recognitionState.value = _recognitionState.value.copy(
                    isListening = false,
                    status = "处理中..."
                )
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                    SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                    SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                    SpeechRecognizer.ERROR_NO_MATCH -> "未识别到语音"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙碌"
                    SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
                    else -> "未知错误"
                }

                _recognitionState.value = _recognitionState.value.copy(
                    isListening = false,
                    status = errorMessage,
                    error = errorMessage
                )
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    _recognitionState.value = _recognitionState.value.copy(
                        isListening = false,
                        recognizedText = recognizedText,
                        status = "识别完成"
                    )
                    processVoiceCommand(recognizedText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _recognitionState.value = _recognitionState.value.copy(
                        partialText = matches[0]
                    )
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _recognitionState.value = _recognitionState.value.copy(
            isListening = false,
            status = "已停止"
        )
    }

    private fun processVoiceCommand(command: String) {
        val commandLower = command.lowercase()

        when {
            commandLower.contains("导航") || commandLower.contains("去") -> {
                extractDestination(command)?.let { dest ->
                    // Start navigation
                    Toast.makeText(this, "导航到: $dest", Toast.LENGTH_SHORT).show()
                }
            }
            commandLower.contains("音乐") || commandLower.contains("播放") -> {
                // Control music
                if (commandLower.contains("暂停") || commandLower.contains("停止")) {
                    // Pause music
                    Toast.makeText(this, "暂停音乐", Toast.LENGTH_SHORT).show()
                } else if (commandLower.contains("下一首")) {
                    Toast.makeText(this, "下一首", Toast.LENGTH_SHORT).show()
                } else if (commandLower.contains("上一首")) {
                    Toast.makeText(this, "上一首", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "播放音乐", Toast.LENGTH_SHORT).show()
                }
            }
            commandLower.contains("电话") || commandLower.contains("拨打") -> {
                // Call functionality
                Toast.makeText(this, "拨打电话", Toast.LENGTH_SHORT).show()
            }
            commandLower.contains("空调") -> {
                // Air conditioning control
                if (commandLower.contains("开")) {
                    Toast.makeText(this, "打开空调", Toast.LENGTH_SHORT).show()
                } else if (commandLower.contains("关")) {
                    Toast.makeText(this, "关闭空调", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "空调控制", Toast.LENGTH_SHORT).show()
                }
            }
            commandLower.contains("首页") || commandLower.contains("桌面") -> {
                // Go to home screen
                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(homeIntent)
            }
            else -> {
                Toast.makeText(this, "未识别的命令: $command", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractDestination(command: String): String? {
        // Simple destination extraction logic
        val keywords = listOf("去", "导航到", "我要去", "到")
        for (keyword in keywords) {
            val index = command.indexOf(keyword)
            if (index != -1) {
                return command.substring(index + keyword.length).trim()
            }
        }
        return null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}

data class VoiceRecognitionState(
    val isListening: Boolean = false,
    val status: String = "",
    val recognizedText: String? = null,
    val partialText: String? = null,
    val error: String? = null
)

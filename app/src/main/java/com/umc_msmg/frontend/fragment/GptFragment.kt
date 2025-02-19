package com.umc_msmg.frontend.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Looper.prepare
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.adapter.AudioVisualizer
import com.umc_msmg.frontend.databinding.GptFragmentBinding
import android.speech.SpeechRecognizer
import com.umc_msmg.frontend.interfaces.ChatMessage
import com.umc_msmg.frontend.interfaces.ChatRequest
import com.umc_msmg.frontend.interfaces.ChatResponse
import com.umc_msmg.frontend.interfaces.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.speech.tts.TextToSpeech
import com.umc_msmg.frontend.interfaces.TTSRequest
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale


class GptFragment : Fragment() {
    private var _binding: GptFragmentBinding? = null
    private val binding get() = _binding!!
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private val chatHistory = mutableListOf<ChatMessage>()
    private var first = true;
    private lateinit var audioVisualizer: AudioVisualizer
    private var mediaPlayer: MediaPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GptFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        initSpeechRecognizer()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }

        // ✅ 버튼 클릭 시 녹음 시작
        binding.btnStart.setOnClickListener {
            //audioVisualizer.startListening()
            startListening()

        }

        // ✅ 버튼 클릭 시 녹음 중지
        binding.btnStop.setOnClickListener {
            //audioVisualizer.stopListening()
            stopListening()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVisualizer.stopListening() // ✅ Fragment가 종료될 때 녹음 정지
        _binding = null
    }

    // ✅ 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            audioVisualizer.startListening()
        }
    }

    private fun startListening() {
        if (isListening) return // ✅ 중복 실행 방지

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // ✅ 실시간 업데이트 활성화
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // ✅ 기본 언어를 영어로 변경

        }

        speechRecognizer?.startListening(intent)
        isListening = true
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }
    }

    private fun initSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        binding.tvText.text = "🎤 듣고 있어요..."
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        binding.tvText.text = "✅ 인식 완료!"
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        binding.tvText.text = "❌ 오류 발생: ${error}"
                        isListening = false
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            sendMessageToChatGPT(matches[0])
                            binding.tvText.text = matches[0] // ✅ 최종 결과 표시
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            binding.tvText.text = matches[0] // ✅ 실시간 업데이트
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }


    private fun sendMessageToChatGPT(userText: String) {

        if(first)
        {
            chatHistory.add(ChatMessage("system", "너는 내가 60대의 노인이고, 신체가 건강한지 잘 모른다고 가정하고 무조건 3번에 걸쳐서 하나의 질문씩 던질꺼야. 질문 내용은 간결해야하고 어르신이 들었을때 이해가 쉬워야해. 3번의 user-assistant간 대화가 끝난 후에는 이사람의 신체 운동 수행능력이 좋으면 '상', 그저 그렇다면 '중', 형편없다면 '하' 라는 단 한글자만 출력하도록해. 말투는 다정하고 정중하며 부드럽게해줘"))
            first = false;
        }
        else {
            chatHistory.add(ChatMessage("user", userText))
        }

        val request = ChatRequest(
            messages = chatHistory )

        // ✅ ChatGPT API 요청 보내기
        RetrofitClient.apiService.getChatResponse(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {

                    val chatResponse = response.body()?.choices?.firstOrNull()?.message?.content
                    chatHistory.add(ChatMessage("assistant", chatResponse ?: "응답 없음"))
                    binding.tvResponse.text = chatResponse ?: "응답 없음"
                    if (chatResponse != null) {
                        generateSpeech(chatResponse)
                    }
                } else {
                    binding.tvResponse.text = "❌ 오류 발생: ${response.errorBody()?.string()}"
                }
            }



            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                binding.tvResponse.text = "❌ 요청 실패: ${t.message}"
            }
        })
    }

    private fun generateSpeech(text: String) {
        val request = TTSRequest(input = text)

        RetrofitClient.ttsService.getSpeech(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { saveAndPlayWav(it) }
                } else {
                    binding.tvResponse.text = "❌ 오류 발생: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.tvResponse.text = "❌ 요청 실패: ${t.message}"
            }
        })
    }

    private fun saveAndPlayWav(body: ResponseBody) {
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "output.wav")

        try {
            val inputStream: InputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()

            playWav(file.absolutePath) // ✅ 저장된 파일을 재생
        } catch (e: Exception) {
            binding.tvResponse.text = "❌ 파일 저장 오류: ${e.message}"
        }
    }

    private fun playWav(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            setVolume(1.0f, 1.0f) // ✅ MediaPlayer 볼륨 최대
            prepare()
            start()
        }
        binding.tvText.text = "🔊 음성 재생 중..."
    }

}

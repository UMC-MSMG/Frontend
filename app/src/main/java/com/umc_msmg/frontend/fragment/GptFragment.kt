package com.umc_msmg.frontend.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Looper.prepare
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.SignUpAddInfoFragment
import com.umc_msmg.frontend.interfaces.TTSRequest
import kotlinx.coroutines.Job
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
    private var mediaPlayer: MediaPlayer? = null
    private var count = 0
    private var corutineJob = Job()
    private var finished = false


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
        sendMessageToChatGPT("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ✅ 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                        binding.tvText.text = "지금 말하세요"
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        binding.tvText.text = "다시 한번 말해주세요"
                        isListening = false
                        startListening()
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
        if(count == 0)
        {
            chatHistory.add(ChatMessage("system", "너는 내가 60대의 노인이고, 신체가 건강한지 잘 모른다고 가정하고 너가 내 건강상태를 대략적으로 파악할때까지 대화를 할꺼야. 질문 내용은 간결해야하고 이해가 쉬워야해. 모든 대화가 끝났다고 판단이 되면 이사람의 신체 운동 수행능력이 좋으면 '상', 그저 중간이면 '중', 낮은수준이면 '하' 라는 단 한 글자만 전달해. 전달할때 오직 한 글자만 전해. 한글자만 말해. 말투는 다소 딱딱하게 해줘. 첫번째 질문 앞에는 꼭 '지금부터 AI 모의검진을 시작하겠습니다.' 하고 줄넘김을 해줘. 모든 대답은 빠르고 간결하게해. 너가 결론이 날때까지 계속 질문하도록. 일반적으로 질문의 개수는 정말 특별한게 아니면 3-7개 사이로 해줘."
            ))
            count++
        }
        else {
            chatHistory.add(ChatMessage("user", userText))
            count++;
        }
        if(finished)
        {
            chatHistory.add(ChatMessage("system", "지금까지 대화를 나누며 내 건강에 관해 느낀점 두줄로 요약해서 보내"))

        }

        val request = ChatRequest(
            messages = chatHistory )

        // ✅ ChatGPT API 요청 보내기
        RetrofitClient.apiService.getChatResponse(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {

                    val chatResponse = response.body()?.choices?.firstOrNull()?.message?.content
                    chatHistory.add(ChatMessage("assistant", chatResponse ?: "응답 없음"))
                    if (chatResponse != null) {
                        binding.ldTv.visibility = VISIBLE
                        if(finished) {
                            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit()
                                .putString("ai_data", chatResponse)
                                .apply()
                        }

                        if(chatResponse.length == 1) {
                            Log.e("!!!!!", chatResponse)
                            finished = true
                            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit()
                                .putString("user_diff", chatResponse) //상/중/하
                                .apply()
                            Log.e("!!!!!", chatResponse)
                            binding.tvText.text = "모든 질문이 끝났어요. 대화 내용을 요약중입니다.\n요약 후 다음화면으로 이동합니다."
                            sendMessageToChatGPT("")
                        }
                        else
                        {
                            binding.tvText.text = chatResponse ?: "응답 없음"
                            generateSpeech(chatResponse)
                        }
                    }
                } else {
                    binding.tvText.text = "❌ 오류 발생: ${response.errorBody()?.string()}"
                }
            }



            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                binding.tvText.text = "❌ 요청 실패: ${t.message}"
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
                    binding.tvText.text = "❌ 오류 발생: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.tvText.text = "❌ 요청 실패: ${t.message}"
            }
        })
    }

    private fun saveAndPlayWav(body: ResponseBody) {
        if (_binding == null) return //
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "output.wav")

        try {
            val inputStream: InputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()

            playWav(file.absolutePath) // ✅ 저장된 파일을 재생
        } catch (e: Exception) {
            binding.tvText.text = "❌ 파일 저장 오류: ${e.message}"
        }
    }

    private fun playWav(filePath: String) {
        binding.ldTv.visibility = GONE
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            setVolume(1.0f, 1.0f) // ✅ MediaPlayer 볼륨 최대
            prepare()
            start()
            setOnCompletionListener {
                if(finished)
                {
                    Handler(Looper.getMainLooper()).postDelayed({
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, SignUpAddInfoFragment())
                            .commit()
                    }, 2500) // 2초 (2000ms)
                }
                else {
                    //binding.tvText.text = "다음 질문을 듣고 있어요..."
                    startListening() // ✅ 음성이 끝나면 자동으로 다시 듣기 시작
                }
            }
        }
        //binding.tvText.text = "음성 재생 중..."
    }


}

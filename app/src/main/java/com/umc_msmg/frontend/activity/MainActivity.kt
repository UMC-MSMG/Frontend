package com.umc_msmg.frontend.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.DailySteps
import com.umc_msmg.frontend.data.DayOfWeek
import com.umc_msmg.frontend.data.MainPageResponse
import com.umc_msmg.frontend.data.WeeklyExerciseSummary
import com.umc_msmg.frontend.databinding.ActivityMainBinding
import com.umc_msmg.frontend.fragment.DiaryFragment
import com.umc_msmg.frontend.fragment.MyPageFragment
import com.umc_msmg.frontend.fragment.ShopFragment
import com.umc_msmg.frontend.fragment.StepperFragment
import com.umc_msmg.frontend.fragment.WorkoutFragment
import com.umc_msmg.frontend.interfaces.InfoLoadData
import com.umc_msmg.frontend.interfaces.InfoUpdateData
import com.umc_msmg.frontend.interfaces.PlacesResponse
import com.umc_msmg.frontend.interfaces.RetrofitClient
import com.umc_msmg.frontend.interfaces.StepRequest
import com.umc_msmg.frontend.util.StepCounterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0;
    private lateinit var sharedPreferences: SharedPreferences
    private var updating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logAllPreferences()
        lifecycleScope.launch {
            loadAndUpdateSP()
            loadandupdatestep()
        }
        if(!updating)
        {
            updating = true;
        }
        requestActivityRecognitionPermission()
        StepCounterManager.resetStepsIfNewDay(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Log.e("StepCounter", "❌ 이 기기는 걸음 센서를 지원하지 않습니다.")
        } else {
            Log.d("StepCounter", "✅ 걸음 센서 사용 가능")
        }

        // 버튼 클릭 이벤트 설정
        setupButtonListeners()




        // 뒤로 가기 버튼 처리
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        updateStepCountUI()
        CoroutineScope(Dispatchers.IO).launch {
            initialLoad()
        }

    }

    private fun updateStepCountUI() {
        runOnUiThread {
            binding.wt.text = stepCount.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadAndUpdateSP()
        }
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepCounter", "✅ 센서 리스너 등록됨")
        }

        loadMyPoints()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            CoroutineScope(Dispatchers.Main).launch {
            loadandupdatestep()}
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun requestActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                101
            )
        }
    }

    private fun setupButtonListeners() {
        binding.exBtn.setOnClickListener {
            switchFragment(WorkoutFragment())
        }

        binding.stepperBtn.setOnClickListener {
            switchFragment(StepperFragment())
        }

        binding.calendar.setOnClickListener {
            switchFragment(DiaryFragment())
        }

        binding.setting.setOnClickListener {
            switchFragment(MyPageFragment())
        }

        binding.shopButton.setOnClickListener {
            switchFragment(ShopFragment())
        }
        binding.pointButton.setOnClickListener {
            switchFragment(ShopFragment())
        }
    }

    private fun switchFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun logAllPreferences() {
        val sharedPreferences =
            this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all

        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "Key: $key, Value: $value")
        }
    }

    fun loadUserInfoAndSteps() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val authorization = "Bearer " + sharedPreferences.getString("access_token", null)
        val name = sharedPreferences.getString("user_name", "사람1")
        var sequenceDays = sharedPreferences.getInt("sequenceDays", 1)
        binding.userStatusText.text = "${name}님은\n${sequenceDays}일째 운동 중이에요."

        UserServiceRetrofitClient.apiService.getMainPage(authorization).enqueue(object : Callback<MainPageResponse> {
            override fun onResponse(call: Call<MainPageResponse>, response: Response<MainPageResponse>) {

                if (response.isSuccessful) {
                    val mainPageData = response.body()
                    if(mainPageData != null) {
                        binding.circularProgressBar.progress = mainPageData.workoutRate
                        Log.d("MainActivity", "목표 달성률 API 호출 성공:  ${mainPageData.workoutRate}")
                    }

                } else {
                    Log.e("MainActivity", "목표 달성률 API 호출 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MainPageResponse>, t: Throwable) {
                Log.e("MainActivity", "목표 달성률 API 호출 실패: ${t.message}")
            }
        })

        UserServiceRetrofitClient.apiService.getWeeklyExerciseSummary(authorization)
            .enqueue(object : Callback<WeeklyExerciseSummary> {
                override fun onResponse(
                    call: Call<WeeklyExerciseSummary>,
                    response: Response<WeeklyExerciseSummary>
                ) {
                    if (response.isSuccessful) {
                        val summary = response.body()
                        sequenceDays = summary?.sequence_days ?: 0
                        Log.d("MainActivity", "loadUserInfoAndSteps - sequenceDays: $sequenceDays")
                        binding.userStatusText.text = "${name}님은 ${sequenceDays}일째 운동 중이에요."
                        if (summary != null) {
                            updateWeeklyCheckboxes(summary)
                        }


                    } else {
                        Log.e("MainActivity", "주간 운동 요약 정보 가져오기 실패: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<WeeklyExerciseSummary>,
                    t: Throwable
                ) {
                    Log.e("MainActivity", "주간 운동 요약 정보 API 호출 실패: ${t.message}")
                }
            })
    }

    private fun loadMyPoints() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val authorization = "Bearer " + sharedPreferences.getString("access_token", null)

        UserServiceRetrofitClient.apiService.getMyPoints(authorization)
            .enqueue(object : Callback<com.umc_msmg.frontend.data.MyPointsResponse> {
                override fun onResponse(call: Call<com.umc_msmg.frontend.data.MyPointsResponse>, response: Response<com.umc_msmg.frontend.data.MyPointsResponse>) {
                    if (response.isSuccessful) {
                        val pointsResponse = response.body()
                        val points = pointsResponse?.point ?: 0
                        binding.myPoint.text = points.toString()
                        Log.d("MainActivity", "포인트 api 성공 :  $points")
                    } else {
                        Log.e("MainActivity", "포인트 가져오기 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<com.umc_msmg.frontend.data.MyPointsResponse>, t: Throwable) {
                    Log.e("MainActivity", "API 호출 실패: ${t.message}")
                }
            })
    }

    private fun updateWeeklyCheckboxes(summary: WeeklyExerciseSummary) {
        val today = Calendar.getInstance()
        val todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK)

        binding.apply {
            calendarCheckMonday.setImageResource(getCheckImageResource(DayOfWeek.MONDAY, summary.monday, todayDayOfWeek))
            calendarCheckTuesday.setImageResource(getCheckImageResource(DayOfWeek.TUESDAY, summary.tuesday, todayDayOfWeek))
            calendarCheckWednesday.setImageResource(getCheckImageResource(DayOfWeek.WEDNESDAY, summary.wednesday, todayDayOfWeek))
            calendarCheckThursday.setImageResource(getCheckImageResource(DayOfWeek.THURSDAY, summary.thursday, todayDayOfWeek))
            calendarCheckFriday.setImageResource(getCheckImageResource(DayOfWeek.FRIDAY, summary.friday, todayDayOfWeek))
            calendarCheckSaturday.setImageResource(getCheckImageResource(DayOfWeek.SATURDAY, summary.saturday, todayDayOfWeek))
            calendarCheckSunday.setImageResource(getCheckImageResource(DayOfWeek.SUNDAY, summary.sunday, todayDayOfWeek))
        }
    }

    private fun getCheckImageResource(day: DayOfWeek, isChecked: Boolean, today: Int): Int {
        val dayOfWeekInt = when (day) {
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
        }

        return if (dayOfWeekInt < today) {
            if (isChecked) R.drawable.calendar_checked else R.drawable.calendar_unchecked
        } else {
            R.drawable.calendar_yet_checked
        }
    }

    suspend fun loadAndUpdateSP() {
        sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = "Bearer " + sharedPreferences.getString("access_token", null)
                if(token == "Bearer ")
                {
                    logout()
                }
                if (token != null) {
                    val response = RetrofitClient.loginService.loadInfo(token)
                    if(response.isSuccessful)
                    {
                        sharedPreferences.edit()
                            .putInt("user_id", response.body()!!.id)
                            .putString("user_name", response.body()!!.name)
                            .putString("user_kakao_id", response.body()!!.kakaoId)
                            .putString("user_gender", response.body()!!.gender)
                            .putString("user_phone", response.body()!!.phoneNumber)
                            .putString("user_birthday", response.body()!!.birthDate)
                            .putInt("user_height", response.body()!!.height)
                            .putInt("user_weight", response.body()!!.weight)
                            .putInt("user_point", response.body()!!.point)
                            .putString("user_image", response.body()!!.image)
                            .putString("user_diff", response.body()!!.workoutLevel)
                            .putString("refresh_token", response.body()!!.refreshToken)
                            .apply()
                        logAllPreferences()
                    }



                    }
                else
                {
                    logout()

                }
            } catch (e: Exception) {
                Log.e("PATCH", "오류 발생: ${e.message}")
            }
        }

        loadUserInfoAndSteps()
    }

    private fun logout() {
        var sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        sharedPreferences = this.getSharedPreferences("LP", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        sharedPreferences = this.getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        clearWebViewData()
        val intent = Intent(this@MainActivity, StartActivity::class.java)
        startActivity(intent)
        Log.d("Logout", "로그아웃 완료 / 웹뷰 데이터 초기화됨")
    }

    private fun clearWebViewData() {
        val webView = WebView(this@MainActivity)
        webView.clearCache(true)
        webView.clearHistory()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }


     suspend fun updateStep(cnt : Int) {
         val sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
         val token = "bearer " + sharedPreferences.getString("access_token", null)
         CoroutineScope(Dispatchers.Main).launch {
             binding.wt.text = (cnt+1).toString()
             Log.d("!!!!!!", "업데이트된")
         }

         CoroutineScope(Dispatchers.IO).launch {
             try {
                 RetrofitClient.loginService.putStep(
                     token,
                     StepRequest(cnt+1, StepCounterManager.getTodayDate())
                 )
             } catch (e: Exception) {
                 Log.e("PATCH", "❌ 오류 발생: ${e.message}")
             }
         }
     }


    suspend fun loadandupdatestep()
    {
        val requestDay = LocalDate.now() // getTodayDate() 대신 LocalDate로 테스트
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val requestDate = requestDay.format(formatter)

        Log.d("!!!!!!", requestDate) // 출력: 2024-02-20
        val token = "Bearer " + sharedPreferences.getString("access_token", null)
        try {
            val response = RetrofitClient.loginService.getSteps(token, requestDate)
            if (response.isSuccessful) {
                val dailySteps = response.body()?.steps

                Log.d("!!!!!!", (dailySteps).toString())

                CoroutineScope(Dispatchers.Main).launch {
                    if (dailySteps != null) {
                        updateStep(dailySteps)
                    }
                }


            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitClient.loginService.putStep(
                            token,
                            StepRequest(1, StepCounterManager.getTodayDate())
                        )
                    } catch (e: Exception) {
                        Log.e("PATCH", "❌ 오류 발생: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "걸음 수 API 호출 실패: ${e.message}")
        }
    }

    private suspend fun initialLoad() {
        val sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreferences.getString("access_token", null)
        try {
            val response = RetrofitClient.loginService.act(token)
            if (response.isSuccessful) {
                Log.e("!!!!", "진짜끝")
            } else {
                Log.e("MainActivity", "가져오기 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "API 호출 실패: ${e.message}")
        }
    }
}

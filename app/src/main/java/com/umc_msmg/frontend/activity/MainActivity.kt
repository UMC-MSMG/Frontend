package com.umc_msmg.frontend.activity

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.DailySteps
import com.umc_msmg.frontend.data.DayOfWeek
import com.umc_msmg.frontend.data.WeeklyExerciseSummary
import com.umc_msmg.frontend.databinding.ActivityMainBinding
import com.umc_msmg.frontend.fragment.DiaryFragment
import com.umc_msmg.frontend.fragment.MyPageFragment
import com.umc_msmg.frontend.fragment.ShopFragment
import com.umc_msmg.frontend.fragment.StepperFragment
import com.umc_msmg.frontend.fragment.WorkoutFragment
import com.umc_msmg.frontend.interfaces.RetrofitClient
import com.umc_msmg.frontend.interfaces.StepRequest
import com.umc_msmg.frontend.util.StepCounterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0;
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logAllPreferences()
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
        sharedPreferences = this.getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)




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
    }

    private fun updateStepCountUI() {
        runOnUiThread {
            binding.wt.text = stepCount.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepCounter", "✅ 센서 리스너 등록됨")
        }
        loadUserInfoAndSteps()
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
            val currentStep = event.values[0].toInt()
            val previousSteps = StepCounterManager.getSteps(this)

            // 새로운 걸음 추가
            stepCount = previousSteps + 1
            Log.d("StepCounter", "📊 오늘 걸음 수: $stepCount")

            // SharedPreferences에 저장
            StepCounterManager.saveSteps(this, stepCount)

            // UI 업데이트
            updateStepCountUI()

            sharedPreferences.edit()
                .putInt("stepCount", stepCount)
                .apply()

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
            val sharedPreferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val token = "bearer " + sharedPreferences.getString("access_token", null)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.loginService.putStep(token,
                        StepRequest(stepCount,StepCounterManager.getTodayDate()))
                } catch (e: Exception) {
                    Log.e("PATCH", "❌ 오류 발생: ${e.message}")
                }
            }

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

    private fun loadUserInfoAndSteps() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val authorization = sharedPreferences.getString("access_token", null) ?: ""
        val name = sharedPreferences.getString("user_name", null) ?: ""
        var sequenceDays = sharedPreferences.getInt("sequenceDays", 1)
        binding.userStatusText.text = "${name}님은\n${sequenceDays}일째 운동 중이에요."

        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = formatter.format(today)

        UserServiceRetrofitClient.apiService.getSteps(authorization, formattedDate)
            .enqueue(object : Callback<DailySteps> {
                override fun onResponse(call: Call<DailySteps>, response: Response<DailySteps>) {
                    if (response.isSuccessful) {
                        val dailySteps = response.body()
                        val steps = dailySteps?.steps ?: 0

                        binding.wt.text = steps.toString()

                    } else {
                        Log.e("MainActivity", "걸음수 가져오기 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<DailySteps>, t: Throwable) {
                    Log.e("MainActivity", "오늘 걸음 수 API 호출 실패: ${t.message}")
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
        val authorization = sharedPreferences.getString("access_token", null) ?: ""

        UserServiceRetrofitClient.apiService.getMyPoints(authorization)
            .enqueue(object : Callback<com.umc_msmg.frontend.data.MyPointsResponse> {
                override fun onResponse(call: Call<com.umc_msmg.frontend.data.MyPointsResponse>, response: Response<com.umc_msmg.frontend.data.MyPointsResponse>) {
                    if (response.isSuccessful) {
                        val pointsResponse = response.body()
                        val points = pointsResponse?.points ?: "0"
                        binding.myPoint.text = points
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
}

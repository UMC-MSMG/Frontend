package com.umc_msmg.frontend.activity

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.TouchDelegate
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.ActivityMainBinding
import com.umc_msmg.frontend.fragment.DiaryFragment
import com.umc_msmg.frontend.fragment.MyPageFragment
import com.umc_msmg.frontend.fragment.ShopFragment
import com.umc_msmg.frontend.fragment.StepperFragment
import com.umc_msmg.frontend.fragment.WorkoutFragment

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0
    private var initialStepCount = -1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logAllPreferences()
        requestActivityRecognitionPermission()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)

        // 센서 매니저 설정
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Log.e("StepCounter", "❌ 이 기기는 걸음 센서를 지원하지 않습니다.")
        } else {
            Log.d("StepCounter", "✅ 걸음 센서 사용 가능")
        }

        // 버튼 클릭 이벤트 설정
        setupButtonListeners()

        // 터치 영역 확장
        expandTouchArea(binding.shopButton, 35)

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
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepCounter", "✅ 센서 리스너 등록됨")
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentStep = event.values[0].toInt()

            if (initialStepCount == -1) {
                initialStepCount = currentStep
            }

            stepCount = currentStep - initialStepCount
            Log.d("StepCounter", "📊 현재 걸음 수: $stepCount")

            // SharedPreferences에 저장
            sharedPreferences.edit().putInt("stepCount", stepCount).apply()

            runOnUiThread {
                binding.wt.text = stepCount.toString()
            }
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

    private fun expandTouchArea(view: View, extraPadding: Int) {
        binding.root.post {
            val parent = view.parent as View
            parent.post {
                val rect = Rect()
                view.getHitRect(rect)
                rect.top -= extraPadding.dpToPx()
                parent.touchDelegate = TouchDelegate(rect, view)
            }
        }
    }

    private fun logAllPreferences() {
        val sharedPreferences =
            this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all

        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "Key: $key, Value: $value")
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}

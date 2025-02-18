// MainActivity.kt
package com.umc_msmg.frontend.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.TouchDelegate
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
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
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // ❌ 권한이 없으면 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                101
            )
        }

        sharedPreferences = getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sharedPreferences.edit().putInt("stepCount", stepCount).apply()



        if (stepSensor == null) {
            Log.e("StepCounter", "❌ 이 기기는 걸음 센서를 지원하지 않습니다.")
        }
        else
        {
            Log.e("StepCounter", "성공띠")
        }


        binding.exBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, WorkoutFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.stepperBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, StepperFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.calendar.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, DiaryFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.setting.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, MyPageFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.exBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, WorkoutFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.shopButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, ShopFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.root.post {
            val parent = binding.shopButton.parent as View
            parent.post {
                val rect = Rect()
                binding.shopButton.getHitRect(rect)
                rect.top -= 35.dpToPx()
                parent.touchDelegate = TouchDelegate(rect, binding.shopButton)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepCounter", "✅ 센서 리스너 등록됨")
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.e("S", "ddmdmdmdmdmdmdmdmdmdmdmdmdmdmmdmdmdmddmdmdmdmdmdm")
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            Log.e("S", "ddmdmdmdmdmdmdmdmdmdmdmdmdmdmmdmdmdmddmdmdmdmdmdm")
            val currentStep = event.values[0].toInt()

            if (initialStepCount == -1) {
                initialStepCount = currentStep // ✅ 앱 실행 시점의 걸음 수 저장
            }

            stepCount = currentStep - initialStepCount
            Log.d("StepCounter", "📊 현재 걸음 수: $stepCount")

            // ✅ SharedPreferences에 저장
            sharedPreferences.edit().putInt("stepCount", stepCount).apply()

            runOnUiThread {
                binding.wt.text = stepCount.toString()
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}




}

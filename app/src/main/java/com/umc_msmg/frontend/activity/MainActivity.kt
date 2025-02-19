// MainActivity.kt
package com.umc_msmg.frontend.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.ActivityMainBinding
import com.umc_msmg.frontend.fragment.DiaryFragment
import com.umc_msmg.frontend.fragment.MyPageFragment
import com.umc_msmg.frontend.fragment.ShopFragment
import com.umc_msmg.frontend.fragment.StepperFragment
import com.umc_msmg.frontend.fragment.WorkoutFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}

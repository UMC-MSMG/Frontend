// StartActivity.kt
package com.umc_msmg.frontend.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.ActivityMainBinding
import com.umc_msmg.frontend.databinding.ActivityStartBinding
import com.umc_msmg.frontend.fragment.MainFragment
import com.umc_msmg.frontend.fragment.MapFragment
import com.umc_msmg.frontend.fragment.PointFragment
import com.umc_msmg.frontend.fragment.StepperStepperFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}

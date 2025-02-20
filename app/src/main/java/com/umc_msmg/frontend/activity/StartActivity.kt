// StartActivity.kt
package com.umc_msmg.frontend.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.commit
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.ActivityStartBinding
import com.umc_msmg.frontend.fragment.SplashFragment
import com.umc_msmg.frontend.service.LocationTrackingService

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.Theme_Msmg)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceIntent = Intent(this, LocationTrackingService::class.java)
        startService(serviceIntent)


        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, SplashFragment())
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}

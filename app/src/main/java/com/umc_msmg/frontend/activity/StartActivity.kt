// StartActivity.kt
package com.umc_msmg.frontend.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.ActivityStartBinding
import com.umc_msmg.frontend.fragment.SplashFragment

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*
        binding.header.btnBack.setOnClickListener {
            onBackPressed()
        }
*/
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

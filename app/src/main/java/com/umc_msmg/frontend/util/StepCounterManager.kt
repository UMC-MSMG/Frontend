package com.umc_msmg.frontend.util

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object StepCounterManager {
    private const val PREF_NAME = "StepPrefs"
    private const val LAST_DATE_KEY = "lastDate"

    // 오늘 날짜 가져오기 (yyyyMMdd)
    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date()) // 예: "20240219"
    }

    // 걸음 수 저장 (날짜별로 저장)
    fun saveSteps(context: Context, steps: Int) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val today = getTodayDate()

        sharedPreferences.edit()
            .putInt(today, steps)
            .putString(LAST_DATE_KEY, today) // 마지막 저장 날짜 업데이트
            .apply()
    }

    // 오늘 걸음 수 가져오기
    fun getSteps(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val today = getTodayDate()
        return sharedPreferences.getInt(today, 0)
    }

    // 날짜가 변경되었는지 확인 후 초기화
    fun resetStepsIfNewDay(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastSavedDate = sharedPreferences.getString(LAST_DATE_KEY, "")
        val today = getTodayDate()

        if (lastSavedDate != today) {
            // 새로운 날이면 걸음 수 초기화
            sharedPreferences.edit()
                .putInt(today, 0)
                .putString(LAST_DATE_KEY, today)
                .apply()
        }
    }

}

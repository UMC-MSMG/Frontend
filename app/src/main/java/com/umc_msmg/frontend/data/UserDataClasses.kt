package com.umc_msmg.frontend.data

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

data class MainPageResponse(
    val sequenceDays: Int,
    val weeklyWorkout: Map<String, Boolean>,
    val workoutRate: Int,
    val point: Int,
    val steps: Int
)

data class UserProfileUpdateRequest(
    val name: String?,
    val gender: String?,
    val height: Int?,
    val weight: Int?,
    val phoneNumber: String?
)

data class DailySteps(
    val userId: String,
    val steps: Int,
    val message: String
)

data class WeeklyExerciseSummary(
    val this_month_earnings: Int,
    val last_month_earnings: Int,
    val sequence_days: Int,
    val sequence_start: String,
    val sequence_end: String,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val workout_level: String
)

data class MyPointsResponse(
    val point: Int
)

data class UpdateProfileResponse(
    val message: String
)

data class WorkoutLevelRequest(
    val workoutLevel: String
)
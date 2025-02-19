package com.umc_msmg.frontend.data

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum class Difficulty {
    상, 중, 하
}

data class Exercise(
    val name: String,
    val set: String? = null,
    val time: String? = null
)

data class WorkoutList(
    val 유산소: List<Exercise>,
    val 근력: List<Exercise>,
    val 유연성: List<Exercise>,
    val 균형: List<Exercise>
)

data class UserProfile(
    val profileImage: String?,
    val name: String?,
    val gender: String?,
    val height: Int?,
    val weight: Int?,
    val phoneNumber: String?,
    val difficulty: Difficulty
)

data class UpdateProfileResponse(
    val message: String
)

object DefaultWorkoutPlan {
    val MONDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("엎드려서 무릎가슴닿기", "", "")),
        근력 = listOf(
            Exercise("스쿼트", "3세트", "15회"),
            Exercise("런지", "3세트", "12회"),
            Exercise("플랭크", "45초"),
            Exercise("사이드 런지", "3세트", "12회")
        ),
        유연성 = listOf(
            Exercise("다리 벌리고 상체 숙이기", "2세트", "20초"),
            Exercise("고양이 자세 스트레칭", "3세트", "10"),
            Exercise("팔꿈치 잡고 어깨 스트레칭", "3세트", "20초")
        ),
        균형 = listOf(
            Exercise("서서 상체 숙이기", "3세트", "20초"),
            Exercise("한 발 앞으로 내밀고 균형 잡기", "3세트", "12초"),
            Exercise("발뒤꿈치 들고 손 머리 위로 올리기", "3세트", "15초"),
            Exercise("서서 허리 돌리며 균형 잡기", "3세트", "15초")
        )
    )
    val MONDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("팔벌려뛰기", "", "20회")),
        근력 = listOf(
            Exercise("의자 끝에 앉으며 스쿼트", "3세트", "15회"),
            Exercise("누워서 엉덩이 들어올리기","", "12회"),
            Exercise("무릎 대고 팔굽혀펴기","", "12회"),
            Exercise("엎드려 다리 뒤로 차기","", "10회")
        ),
        유연성 = listOf(
            Exercise("손 머리 위로 올리기", "2세트", "15초"),
            Exercise("손끝으로 발끝 터치", "2세트", "15초"),
            Exercise("몸통 비틀기", "2세트", "12회")
        ),
        균형 = listOf(
            Exercise("서서 허리 돌리기", "3세트", "12초"),
            Exercise("벽 잡고 까치발 서기", "3세트", "12초"),
            Exercise("한 발 들고 무릎 펴기", "3세트", "12초"),
            Exercise("한 발을 옆으로 들고 버티기", "3세트", "15초")
        )
    )
    val MONDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("빠르게 걷기", "3분")),
        근력 = listOf(
            Exercise("발뒤꿈치 올리기", "", "12회"),
            Exercise("다리 차올리기", "", "8회"),
            Exercise("다리 옆으로 올리기", "", "8회")
        ),
        유연성 = listOf(
            Exercise("앉아서 상체 숙이기", "2세트", "15초"),
            Exercise("의자에 앉아 다리 교차", "2세트", "15초"),
            Exercise("한쪽 다리 앓게 펴기"),
            Exercise("손 깍지 끼고 머리 위로 뻗기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "3세트", "10초"),
            Exercise("발끝 들기", "3세트", "12초"),
            Exercise("벽 잡고 무릎 올리기", "3세트", "10초"),
            Exercise("한 발 들고 몸통 비틀기", "3세트", "12초")
        )
    )
    val TUESDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("제자리 무릎 들어올리기", "", "5분")),
        근력 = listOf(
            Exercise("런지", "3세트", "15회"),
            Exercise("사이드 런지", "3세트", "12회"),
            Exercise("플랭크 변형", "3세트", "15초")
        ),
        유연성 = listOf(
            Exercise("한쪽 다리 옆으로 벌려 벋기기", "3세트", "12초"),
            Exercise("고양이 자세 스트레칭", "3세트", "15초"),
            Exercise("등 뒤로 손 깍지 끼기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("한 발 앞으로 내밀고 균형 잡기", "3세트", "15초"),
            Exercise("발 뒤꿈치 들고 손 머리 위로 올리기", "3세트", "15초"),
            Exercise("한 발로 걷기 연습", "3세트", "15초")
        )
    )
    val TUESDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("제자리 무릎 들어올리기", "3분")),
        근력 = listOf(
            Exercise("번갈아 옆굽힘 펴기", "3세트", "12회"),
            Exercise("계단 오르기 동작", "3세트", "10회"),
            Exercise("의자 팔굽혀펴기", "3세트", "12회")
        ),
        유연성 = listOf(
            Exercise("손 머리 뒤로 스트레칭", "3세트", "15초"),
            Exercise("팔꿈치 잡고 스트레칭", "3세트", "15초"),
            Exercise("등 뒤로 손 깍지 끼기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("한 발 앞으로 내밀고 균형 잡기", "3세트", "15초"),
            Exercise("무릎 굽힌채로 버티기", "3세트", "15초"),
            Exercise("발뒤꿈치 들기 + 양손 올리기", "3세트", "15초")
        )
    )
    val TUESDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("제자리 걷기", "3분")),
        근력 = listOf(
            Exercise("의자에서 천천히 일어나기", "2세트", "8회"),
            Exercise("다리 뒤로 차기", "2세트", "8회"),
            Exercise("팔 굽혔다 펴기 (벽 활용 X)", "3세트", "8회"),
            Exercise("다리 차올리기", "2세트", "8회")
        ),
        유연성 = listOf(
            Exercise("손끝으로 발끝 터치", "2세트", "15초"),
            Exercise("어깨 돌리기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("벽 잡고 까치발 서기", "2세트", "10초"),
            Exercise("발뒤꿈치 들기", "2세트", "10회"),
            Exercise("의자에 앉아 다리 교차", "2세트", "10초"),
            Exercise("발 뒤꿈치 들고 버티기", "2세트", "10초")
        )
    )

    val WEDNESDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("제자리 높이 뛰기 + 팔 올리기", "5분")),
        근력 = listOf(
            Exercise("스쿼트", "3세트", "15회"),
            Exercise("런지", "3세트", "15회"),
            Exercise("플랭크 변형", "3세트", "20초")
        ),
        유연성 = listOf(
            Exercise("서서 상체 숙이기", "3세트", "20초"),
            Exercise("고양이 자세 스트레칭", "3세트", "15초"),
            Exercise("앉은 자세 머리 올리기 기울이기", "3세트", "20초")
        ),
        균형 = listOf(
            Exercise("서서 발 교차하며 균형 잡기", "3세트", "15초"),
            Exercise("한 발로 걷기 연습", "3세트", "15초"),
            Exercise("한 발로 몸통 비틀기", "3세트", "15초")
        )
    )
    val WEDNESDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("빠르게 걷기", "5분")),
        근력 = listOf(
            Exercise("의자에서 두 손으로 앞으로 뻗으며 천천히 일어나기", "3세트", "12회"),
            Exercise("한쪽 다리 뒤로 뻗으며 다리 들어올려 천천히 내리기", "3세트", "12회"),
            Exercise("무릎을 바닥에 대고 팔굽혀펴기", "3세트", "10회")
        ),
        유연성 = listOf(
            Exercise("다리 벌려 앉아 숙이기", "3세트", "20초"),
            Exercise("몸통 비틀기", "3세트", "15초"),
            Exercise("등 뒤로 손 깍지 끼기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("서서 한 발 앞으로 내밀고 균형 잡기", "3세트", "15초"),
            Exercise("발뒤꿈치 들기 + 팔 머리 위로 올리기", "3세트", "15초"),
            Exercise("서서 발 교차하여 균형 잡기", "3세트", "15초")
        )
    )
    val WEDNESDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("제자리 걷기", "", "3분")),
        근력 = listOf(
            Exercise("의자에서 천천히 일어나기", "3세트", "10회"),
            Exercise("다리 뒤로 차기", "3세트", "8회"),
            Exercise("팔 굽혔다 펴기 (벽 활용 X)", "3세트", "8회"),
            Exercise("다리 차올리기", "2세트", "8회")
        ),
        유연성 = listOf(
            Exercise("앉아서 상체 숙이기", "2세트", "15초"),
            Exercise("손끝으로 발끝 터치", "2세트", "15초"),
            Exercise("손 머리 위로 뻗기", "2세트", "15초")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "3세트", "10초"),
            Exercise("발뒤꿈치 들기", "3세트", "10회"),
            Exercise("한 발 옆으로 들고 균형 잡기", "3세트", "10초"),
            Exercise("두 발 모아 옆으로 젖혀 들기", "3세트", "10초")
        )
    )

    val THURSDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("빠르게 걷기", "", "5분")),
        근력 = listOf(
            Exercise("스쿼트", "3세트", "15회"),
            Exercise("런지", "3세트", "15회"),
            Exercise("플랭크 변형", "3세트", "20초")
        ),
        유연성 = listOf(
            Exercise("서서 상체 숙이기", "3세트", "20초"),
            Exercise("양손 머리 위로 올리고 몸 기울이기", "3세트", "20초"),
            Exercise("등 뒤로 손 깍지 끼기", "3세트", "15초"),
            Exercise("손 깍지 끼고 머리 위로 뻗기", "3세트", "20초")
        ),
        균형 = listOf(
            Exercise("한쪽 다리로 서서 균형 잡기", "3세트", "15초"),
            Exercise("발끝 벌려 + 양손 머리 위로 올리기", "3세트", "15초"),
            Exercise("서서 발 교차하며 균형 잡기", "3세트", "15초"),
            Exercise("한 발로 걷기 연습", "3세트", "15초")
        )
    )
    val THURSDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("박자 맞춰 걷기", "", "3분")),
        근력 = listOf(
            Exercise("의자에서 두 손으로 앞으로 뻗으며 일어나기", "3세트", "12회"),
            Exercise("무릎을 바닥에 대고 팔굽혀펴기", "3세트", "10회"),
            Exercise("무릎 올리기", "3세트", "10회")
        ),
        유연성 = listOf(
            Exercise("손끝으로 발끝 터치", "2세트", "15초"),
            Exercise("다리 벌려 앉아 숙이기", "3세트", "20초"),
            Exercise("몸통 비틀기", "3세트", "12초"),
            Exercise("허리 돌리기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "3세트", "10초"),
            Exercise("발뒤꿈치 들기", "3세트", "10회"),
            Exercise("한쪽 다리를 옆으로 뻗고 균형 잡기", "3세트", "10초"),
            Exercise("발을 뻗어 엇갈려 균형 잡기", "3세트", "15초")
        )
    )
    val THURSDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("제자리 걷기", "3분")),
        근력 = listOf(
            Exercise("의자에서 천천히 일어나기", "3세트", "10회"),
            Exercise("다리 뒤로 차기", "3세트", "8회"),
            Exercise("팔 굽혔다 펴기 (벽 활용 X)", "3세트", "8회"),
            Exercise("다리 차올리기", "2세트", "8회")
        ),
        유연성 = listOf(
            Exercise("앉아서 상체 숙이기", "2세트", "15초"),
            Exercise("고양이 자세 스트레칭", "2세트", "15초"),
            Exercise("팔꿈치 잡고 스트레칭", "2세트", "15초"),
            Exercise("어깨 돌리기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "3세트", "10초"),
            Exercise("발뒤꿈치 들기", "3세트", "10회"),
            Exercise("한쪽 다리를 옆으로 뻗고 균형 잡기", "3세트", "10초"),
            Exercise("두 발 모아 옆으로 젖혀 들기", "3세트", "10초")
        )
    )

    val FRIDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("스텝 터치 + 팔 올리기", "", "5분")),
        근력 = listOf(
            Exercise("스쿼트", "3세트", "15회"),
            Exercise("런지", "3세트", "15회"),
            Exercise("정자세 팔굽혀펴기", "3세트", "12회")
        ),
        유연성 = listOf(
            Exercise("다리 벌려 서서 상체 숙이기", "3세트", "20초"),
            Exercise("앉아서 허리 돌리기", "3세트", "20초"),
            Exercise("등 뒤로 손 깍지 끼기", "3세트", "15초"),
            Exercise("손 깍지 끼고 머리 위로 뻗기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("한쪽 다리로 서서 균형 잡기", "3세트", "15초"),
            Exercise("발끝 벌려 + 팔 머리 위로 올리기", "3세트", "15초"),
            Exercise("한 다리 옆으로 뻗고 균형 잡기", "3세트", "15초"),
            Exercise("한 발로 몸통 비틀기", "3세트", "15초")
        )
    )
    val FRIDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("빠르게 제자리 걷기", "",  "5분")),
        근력 = listOf(
            Exercise("앉은 자세로 한쪽 다리 들기", "3세트", "12회"),
            Exercise("다리를 번갈아 뒤로 들기", "3세트", "12회"),
            Exercise("무릎을 바닥에 대고 팔굽혀펴기", "3세트", "10회")
        ),
        유연성 = listOf(
            Exercise("앉은 자세에서 상체 숙이기", "2세트", "20초"),
            Exercise("손 머리 위로 올리고 몸 기울이기", "3세트", "20초"),
            Exercise("허리 돌리기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("벽 잡고 까치발 서기", "3세트", "15초"),
            Exercise("무릎 굽힌채로 버티기", "3세트", "15초"),
            Exercise("발을 뻗어 엇갈려 균형 잡기", "3세트", "15초")
        )
    )
    val FRIDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("제자리 걷기", "", "3분")),
        근력 = listOf(
            Exercise("의자에서 천천히 일어나기", "3세트", "10회"),
            Exercise("다리 뒤로 차기", "3세트", "8회"),
            Exercise("팔 굽혔다 펴기 (벽 활용 X)", "3세트", "8회")
        ),
        유연성 = listOf(
            Exercise("손끝으로 발끝 터치", "2세트", "15초"),
            Exercise("고양이 자세 스트레칭", "3세트", "15초"),
            Exercise("어깨 돌리기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "3세트", "10초"),
            Exercise("발뒤꿈치 들기", "3세트", "10회"),
            Exercise("한쪽 다리를 옆으로 뻗고 균형 잡기", "3세트", "10초"),
            Exercise("두 발 모아 옆으로 젖혀 들기", "3세트", "10초")
        )
    )

    val SATURDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("빠르게 걷기", "5분")),
        근력 = listOf(
            Exercise("스쿼트", "2세트", "12회")
        ),
        유연성 = listOf(
            Exercise("고양이 자세 스트레칭", "3세트", "15초"),
            Exercise("손 깍지 끼고 머리 위로 뻗기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("한 발로 서서 균형 잡기", "2세트", "12초"),
            Exercise("한쪽 다리로 걷기 연습", "3세트", "15초")
        )
    )
    val SATURDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("가볍게 제자리 걷기", "5분")),
        근력 = listOf(
            Exercise("의자에서 다리 뻗고 들기", "2세트", "10회")
        ),
        유연성 = listOf(
            Exercise("다리 벌려 앉아 숙이기", "2세트", "15초") ,
            Exercise("어깨 돌리기", "2세트", "15초")
        ),
        균형 = listOf(
            Exercise("벽 잡고 까치발 서기", "2세트", "10초"),
            Exercise("발꿈치 들기 + 팔 벌리기", "2세트", "10회"),
            Exercise("의자에 앉아 다리 교차", "2세트", "10초")
        )
    )
    val SATURDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("제자리 걷기", "3분")),
        근력 = listOf(
            Exercise("의자에서 천천히 일어나기", "2세트", "8회")
        ),
        유연성 = listOf(
            Exercise("앉아서 상체 숙이기", "2세트", "15초"),
            Exercise("손끝으로 발끝 터치", "2세트", "15초")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "2세트", "8회"),
            Exercise("발뒤꿈치 들기", "2세트", "10회"),
            Exercise("코로 깊게 숨 들이쉬고 입으로 천천히 내쉬기", "5분")
        )
    )

    val SUNDAY_상 = WorkoutList(
        유산소 = listOf(Exercise("빠르게 걷기", "5분")),
        근력 = listOf(
            Exercise("스쿼트 + 팔 앞으로 뻗기", "2세트", "12회") ,
            Exercise("런지 변형", "2세트", "12회")
        ),
        유연성 = listOf(
            Exercise("고양이 자세 스트레칭", "3세트", "15초"),
            Exercise("손 깍지 끼고 머리 위로 뻗기", "3세트", "15초")
        ),
        균형 = listOf(
            Exercise("한 발로 서서 균형 잡기", "2세트", "12초"),
            Exercise("서서 발 교차하며 균형 잡기", "2세트", "12초")
        )
    )
    val SUNDAY_중 = WorkoutList(
        유산소 = listOf(Exercise("가볍게 제자리 걷기", "5분")),
        근력 = listOf(
            Exercise("무릎 올리기", "2세트", "10회")
        ),
        유연성 = listOf(
            Exercise("앉아서 팔꿈치 잡고 몸 비틀기", "3세트", "15초"),
            Exercise("앉아서 눈 감아 목 돌리기", "5분")
        ),
        균형 = listOf(
            Exercise("발뒤꿈치 들기 + 팔 벌리기", "2세트", "10회"),
            Exercise("한 발로 걷기 연습", "2세트", "10초")
        )
    )
    val SUNDAY_하 = WorkoutList(
        유산소 = listOf(Exercise("천천히 제자리 걷기", "3분")),
        근력 = listOf(
            Exercise("다리 교차 들기", "2세트", "8회")
        ),
        유연성 = listOf(
            Exercise("손끝으로 발끝 터치", "2세트", "15초"),
            Exercise("코로 깊게 숨 들이쉬고 입으로 천천히 내쉬기", "5분")
        ),
        균형 = listOf(
            Exercise("의자 잡고 한쪽 다리 들기", "2세트", "8회"),
            Exercise("가볍게 스트레칭하면서 명상", "5분")
        )
    )

    fun getWorkoutList(dayOfWeek: DayOfWeek, difficulty: Difficulty): WorkoutList {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> when (difficulty) {
                Difficulty.상 -> MONDAY_상
                Difficulty.중 -> MONDAY_중
                Difficulty.하 -> MONDAY_하
            }
            DayOfWeek.TUESDAY -> when (difficulty) {
                Difficulty.상 -> TUESDAY_상
                Difficulty.중 -> TUESDAY_중
                Difficulty.하 -> TUESDAY_하
            }
            DayOfWeek.WEDNESDAY -> when (difficulty) {
                Difficulty.상 -> WEDNESDAY_상
                Difficulty.중 -> WEDNESDAY_중
                Difficulty.하 -> WEDNESDAY_하
            }
            DayOfWeek.THURSDAY -> when (difficulty) {
                Difficulty.상 -> THURSDAY_상
                Difficulty.중 -> THURSDAY_중
                Difficulty.하 -> THURSDAY_하
            }
            DayOfWeek.FRIDAY -> when (difficulty) {
                Difficulty.상 -> FRIDAY_상
                Difficulty.중 -> FRIDAY_중
                Difficulty.하 -> FRIDAY_하
            }
            DayOfWeek.SATURDAY -> when (difficulty) {
                Difficulty.상 -> SATURDAY_상
                Difficulty.중 -> SATURDAY_중
                Difficulty.하 -> SATURDAY_하
            }
            DayOfWeek.SUNDAY -> when (difficulty) {
                Difficulty.상 -> SUNDAY_상
                Difficulty.중 -> SUNDAY_중
                Difficulty.하 -> SUNDAY_하
            }

            else -> MONDAY_중
        }
    }
}
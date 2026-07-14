package com.umc_msmg.frontend.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    // 기본 정보
    val userName = MutableLiveData<String>()
    val userGender = MutableLiveData<String>()
    val birthDate = MutableLiveData<Long>()
    val phoneNumber = MutableLiveData<String>()

    // 추가 정보
    val height = MutableLiveData<Int>()
    val weight = MutableLiveData<Int>()
    val medications = MutableLiveData<List<String>>()

    // 약관 동의
    val termsAgreed = MutableLiveData<Boolean>()
}

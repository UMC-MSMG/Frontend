package com.umc_msmg.frontend.data

data class Medication(
    val medName: String,
    val description: String,
    val times: List<String>,
    val days: List<String>
)

data class UpdateMedicationsRequest(
    val medications: List<Medication>
)

data class UpdateMedicationsResponse(
    val message: String
)
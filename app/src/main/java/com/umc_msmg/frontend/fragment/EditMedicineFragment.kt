package com.umc_msmg.frontend.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.adapter.MedicationAdapter
import com.umc_msmg.frontend.data.Medication
import com.umc_msmg.frontend.data.UpdateMedicationsRequest
import com.umc_msmg.frontend.databinding.FragmentEditMedicineBinding
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMedicineFragment : Fragment() {
    private var _binding: FragmentEditMedicineBinding? = null
    private val binding get() = _binding!!

    private lateinit var medicationAdapter: MedicationAdapter
    private var medications: MutableList<Medication> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        medicationAdapter = MedicationAdapter(medications) { position ->
            removeMedication(position)
        }
        binding.medicationRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicationAdapter
        }

        // 사용자 복용 약 정보 불러오기
        loadMedications()

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            saveMedications()
        }

        binding.addMedicineButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddMedicineFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun removeMedication(position: Int) {
        medicationAdapter.removeItem(position)
    }

    private fun loadMedications() {
        // 임시 인증 토큰
        val authorization = "Bearer YOUR_JWT_TOKEN"

        UserServiceRetrofitClient.apiService.updateMedications(authorization, UpdateMedicationsRequest(medications)).enqueue(object : Callback<com.umc_msmg.frontend.data.UpdateMedicationsResponse> {
            override fun onResponse(call: Call<com.umc_msmg.frontend.data.UpdateMedicationsResponse>, response: Response<com.umc_msmg.frontend.data.UpdateMedicationsResponse>) {
                if (response.isSuccessful) {
                    //medications = response.body()
                    Log.d("EditMedicineFragment", "API 호출 성공")
                } else {
                    // API 호출 실패
                    Log.e("EditMedicineFragment", "API 호출 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<com.umc_msmg.frontend.data.UpdateMedicationsResponse>, t: Throwable) {
                // 네트워크 오류 등
                Log.e("EditMedicineFragment", "API 호출 실패: ${t.message}")
            }
        })

        medications.add(Medication("test", "123", listOf("123"), listOf("123")))
        medicationAdapter.notifyDataSetChanged()

    }

    private fun saveMedications() {
        // 임시 인증 토큰
        val authorization = "Bearer YOUR_JWT_TOKEN"
        UserServiceRetrofitClient.apiService.updateMedications(authorization, UpdateMedicationsRequest(medications)).enqueue(object : Callback<com.umc_msmg.frontend.data.UpdateMedicationsResponse> {
            override fun onResponse(call: Call<com.umc_msmg.frontend.data.UpdateMedicationsResponse>, response: Response<com.umc_msmg.frontend.data.UpdateMedicationsResponse>) {
                if (response.isSuccessful) {
                    //medications = response.body()
                    Log.d("EditMedicineFragment", "API 호출 성공")
                } else {
                    // API 호출 실패
                    Log.e("EditMedicineFragment", "API 호출 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<com.umc_msmg.frontend.data.UpdateMedicationsResponse>, t: Throwable) {
                // 네트워크 오류 등
                Log.e("EditMedicineFragment", "API 호출 실패: ${t.message}")
            }
        })

        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
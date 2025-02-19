package com.umc_msmg.frontend.fragment

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.MapFragmentBinding
import com.umc_msmg.frontend.interfaces.PlacesResponse
import com.umc_msmg.frontend.interfaces.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationList = mutableListOf<LatLng>() // ✅ 저장된 위치 리스트
    private var polyline: Polyline? = null
    private lateinit var placesClient: PlacesClient
    private var _binding: MapFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var target : LatLng
    private val markerList = mutableListOf<com.google.android.gms.maps.model.Marker>() // ✅ 마커 저장 리스트


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearAllSavedLocations()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyCwtcCe2c256EzXtZ5yo__O9lpTf9ktN3A")
        }
        placesClient = Places.createClient(requireContext())

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        map.isMyLocationEnabled = true
        val savedTargetLocation = sharedPreferences.getString("targetLocation", "")
        if (savedTargetLocation != null) {
            Log.d("S", savedTargetLocation)
        }
        if(savedTargetLocation == "")
        {
            getCurrentLocation()
        }
        else
        {
            val savedLocation = sharedPreferences.getString("location", "")
            val marker = map.addMarker(
                MarkerOptions()
                    .position(stringToLatLng(savedLocation.toString()))
                    .title(savedTargetLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            )
            marker?.let { markerList.add(it) } // ✅ 리스트에 저장

            val fragmentB = requireActivity().supportFragmentManager.findFragmentByTag("FragmentBTag") as? StepperStepperFragment
            fragmentB?.setTargetText()
        }
        loadSavedLocations() // ✅ 기존 경로 불러오기
        drawPolyline() // ✅ 기존 Polyline 그리기
        setupLiveLocationUpdates() // ✅ 실시간 위치 업데이트

    }

    override fun onResume() {
        super.onResume()
        setupLiveLocationUpdates() // ✅ Fragment가 다시 활성화될 때 위치 업데이트 다시 시작
    }

    private fun loadSavedLocations() {
        val storedLocations = sharedPreferences.getString("KEY_ROUTE", "") ?: ""
        Log.d("S", storedLocations)

        locationList.clear()
        storedLocations.split(";").forEach {
            val latLng = it.removeSurrounding("LatLng(", ")").split(",")
            if (latLng.size == 2) {
                locationList.add(LatLng(latLng[0].toDouble(), latLng[1].toDouble()))
            }
        }
        Log.d("MapsFragment", "저장된 위치 개수: ${locationList.size}")
    }

    private fun drawPolyline() {
        if (locationList.isEmpty()) return

        polyline?.remove() // ✅ 기존 선 삭제
        polyline = map.addPolyline(
            PolylineOptions()
                .addAll(locationList)
                .width(10f)
                .color(0xFF00AAFF.toInt())
        )

        // ✅ 마지막 위치로 카메라 이동
        locationList.lastOrNull()?.let {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, map.cameraPosition.zoom))
        }
    }

    private fun setupLiveLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 2000  // ✅ 2초마다 위치 업데이트
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    saveAndUpdateLocation(location)
                    checkArrived()
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun saveAndUpdateLocation(location: Location) {
        val newLatLng = LatLng(location.latitude, location.longitude)

        // ✅ 위치 저장
        val sharedPreferences = context?.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val storedLocations = sharedPreferences?.getString("KEY_ROUTE", "") ?: ""
        val updatedLocations = if (storedLocations.isEmpty()) "$newLatLng" else "$storedLocations;$newLatLng"

        sharedPreferences?.edit()?.putString("KEY_ROUTE", updatedLocations)?.apply()

        // ✅ Polyline 확장 (이전 위치부터 현재 위치까지 선 추가)
        locationList.add(newLatLng)
        polyline?.remove()
        polyline = map.addPolyline(
            PolylineOptions()
                .addAll(locationList)
                .width(10f)
                .color(0xFF00AAFF.toInt())
        )

        // ✅ 지도 카메라 이동 (현재 위치 중심)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, map.cameraPosition.zoom))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback) // ✅ 메모리 누수 방지
    }

    fun getCurrentLocation() {
        if (!::map.isInitialized) {
            Log.e("MapFragment", "❌ getCurrentLocation() 호출 전에 map이 초기화되지 않음")
            return
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, map.cameraPosition.zoom))
                    searchNearbyParks(currentLatLng) // ✅ 1km 반경 공원 검색
                }
            }
        }
    }

    fun myLocation() : LatLng {
        var currentLatLng = LatLng(0.0,0.0)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatLng = LatLng(it.latitude, it.longitude)

                }
            }
        }
        return currentLatLng
    }


    private fun searchNearbyParks(currentLatLng: LatLng) {
        val location = "${currentLatLng.latitude},${currentLatLng.longitude}"
        val radius = 1000 // ✅ 반경 10km

        RetrofitClient.mapApiService.getNearbyParks(location, radius, "park", "AIzaSyCwtcCe2c256EzXtZ5yo__O9lpTf9ktN3A").enqueue(object :
            Callback<PlacesResponse> {
            override fun onResponse(call: Call<PlacesResponse>, response: Response<PlacesResponse>) {
                if (response.isSuccessful) {
                    val parkList = mutableListOf<Pair<String, LatLng>>() // ✅ 공원 리스트 저장용

                    response.body()?.results?.forEach { place ->
                        val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                        parkList.add(Pair(place.name, latLng))
                        Log.d("PlacesAPI", "🌳 공원 발견: ${place.name} (위치: ${latLng.latitude}, ${latLng.longitude})")
                    }

                    // ✅ 랜덤으로 공원 선택 후 마커 추가
                    if (parkList.isNotEmpty()) {
                        val randomPark = parkList.random()
                        Log.d("PlacesAPI", "🎯 선택된 공원: ${randomPark.first}, 위치: ${randomPark.second}")

                        val sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        val latLngString = "${randomPark.second.latitude},${randomPark.second.longitude}"
                        editor.putString("targetLocation", randomPark.first)
                        editor.putString("location", latLngString)
                        editor.apply()

                        // ✅ 지도에 마커 추가
                        val marker = map.addMarker(
                            MarkerOptions()
                                .position(randomPark.second)
                                .title(randomPark.first)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )
                        marker?.let { markerList.add(it) }

                        val stepperFragment = requireActivity().supportFragmentManager.findFragmentByTag("StepTag") as? StepperStepperFragment
                        stepperFragment?.setTargetText()
                    } else {
                        Log.d("PlacesAPI", "❌ 반경 ${radius}m 이내에 공원이 없습니다.")
                    }
                } else {
                    Log.e("PlacesAPI", "❌ API 응답 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                Log.e("PlacesAPI", "❌ 네트워크 요청 실패: ${t.message}")
            }
        })
    }




    private fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
        return results[0] // 거리(m)
    }

    private fun clearAllSavedLocations() {
        val sharedPreferences = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // ✅ 모든 데이터 삭제
        Log.d("SharedPreferences", "모든 위치 데이터가 삭제되었습니다.")
    }

    fun stringToLatLng(latLngString: String): LatLng {
        return try {
            val parts = latLngString.split(",") // ✅ 쉼표(,)로 나누기
            if (parts.size == 2) {
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                LatLng(latitude, longitude) // ✅ 변환 성공
            } else {
                LatLng(0.0, 0.0) // ❌ 변환 실패 시 기본값 반환
            }
        } catch (e: Exception) {
            Log.e("stringToLatLng", "❌ 변환 실패: ${e.message}")
            LatLng(0.0, 0.0) // ❌ 예외 발생 시 기본값 반환
        }
    }


    fun checkArrived()
    {
        val savedTargetLocation = sharedPreferences.getString("location", "")
        if(calculateDistance(myLocation(), stringToLatLng(savedTargetLocation.toString()))<=50)
        {
            Log.e("!!!!!","도착띠")
            removeAllMarkers()
            val stepperFragment = requireActivity().supportFragmentManager.findFragmentByTag("StepTag") as? StepperStepperFragment
            stepperFragment?.arrived()
        }
        else
        {
            Log.e("!!!!!", "안도착띠 ㅠ")
        }
    }

    fun removeAllMarkers() {
        requireActivity().runOnUiThread {
            Log.d("MapFragment", "🟢 현재 마커 개수: ${markerList.size}")

            if (markerList.isEmpty()) {
                Log.w("MapFragment", "⚠️ 삭제할 마커가 없습니다!")
            }

            for (marker in markerList) {
                marker.remove()
                Log.d("MapFragment", "🗑 마커 삭제됨: ${marker.position}")
            }

            markerList.clear()
            Log.d("MapFragment", "🔥 모든 마커가 삭제되었습니다!")
        }
    }
}

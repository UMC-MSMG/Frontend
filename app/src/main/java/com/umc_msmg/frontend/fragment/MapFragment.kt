package com.umc_msmg.frontend.fragment

import android.Manifest
import android.content.Context
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

class MapFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationList = mutableListOf<LatLng>() // ✅ 저장된 위치 리스트
    private var polyline: Polyline? = null
    private lateinit var placesClient: PlacesClient
    private var _binding: MapFragmentBinding? = null
    private val binding get() = _binding!!



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearAllSavedLocations()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

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

        val sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
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
            map.addMarker(
                MarkerOptions()
                    .position(stringToLatLng(savedLocation.toString()))
                    .title(savedTargetLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
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

        val sharedPreferences = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 17f))
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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    searchNearbyParks(currentLatLng) // ✅ 1km 반경 공원 검색
                }
            }
        }
    }

    private fun searchNearbyParks(currentLatLng: LatLng) {
        val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                val parkList = mutableListOf<Pair<String, LatLng>>() // ✅ 공원 리스트 저장용

                for (placeLikelihood in response.placeLikelihoods) {
                    val place = placeLikelihood.place
                    val latLng = place.latLng
                    val placeName = place.name ?: "알 수 없음"

                    if (latLng != null && !placeName.contains(Regex("\\d"))) { // 🔹 이름에 숫자가 포함되지 않은 경우만 추가
                        val distance = calculateDistance(currentLatLng, latLng)
                        if (distance <= 1000) { // ✅ 반경 1km 이내인 경우
                            parkList.add(Pair(placeName, latLng))
                            Log.d("PlacesAPI", "공원 발견: $placeName (거리: ${distance}m)")
                        }
                    }
                }


                // ✅ 랜덤으로 공원 하나 선택 후 로그 출력
                if (parkList.isNotEmpty()) {
                    val randomPark = parkList.random()
                    Log.d("PlacesAPI", "🎯 랜덤 선택된 공원: ${randomPark.first}, 위치: ${randomPark.second}")
                    val sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    val latLngString = "${randomPark.second.latitude},${randomPark.second.longitude}" // ✅ `String` 형태로 변환
                    editor.putString("targetLocation", randomPark.first)
                    editor.putString("location", latLngString)
                    val success = editor.commit() // ✅ 즉시 저장

                    if (success) {
                        Log.d("SharedPreferences", "🎯 targetLocation 저장 완료: ${randomPark.first}")
                    } else {
                        Log.e("SharedPreferences", "❌ targetLocation 저장 실패")
                    }

                    map.addMarker(
                        MarkerOptions()
                            .position(randomPark.second)
                            .title(randomPark.first)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )

                } else {
                    Log.d("PlacesAPI", "❌ 반경 1km 이내에 공원이 없습니다.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PlacesAPI", "공원 검색 실패: ${exception.message}")
            }
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



}

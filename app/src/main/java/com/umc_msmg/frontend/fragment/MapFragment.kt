package com.umc_msmg.frontend.fragment

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.*
import com.kakao.vectormap.shape.*
import com.umc_msmg.frontend.R

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private var map: KakaoMap? = null
    private var trackingManager: TrackingManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mv)
        KakaoMapSdk.init(requireContext(), "b20c9e88746f0241b52beeabf5618f1f")

        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(p0: Exception?) {
                    Log.e("KakaoMap", "지도 초기화 실패: ${p0?.message}")
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    Log.d("KakaoMap", "지도 준비 완료")
                    map = kakaoMap
                    trackingManager = kakaoMap.trackingManager

                    if (checkLocationPermission()) {
                        setupMap(kakaoMap)
                    } else {
                        Log.d("Permission", "위치 권한 없음, 권한 요청 시작")
                        requestLocationPermission()
                    }
                }
            }
        )
    }

    // ✅ 지도 설정 함수 (분리하여 가독성 향상)
    private fun setupMap(kakaoMap: KakaoMap) {
        val position = kakaoMap.cameraPosition?.position ?: LatLng.from(37.5665, 126.9780) // 기본 위치 (서울)

        // ✅ 카메라 이동
        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position))

        // ✅ 현재 위치를 표시할 Label 생성
        val labelLayer = kakaoMap.labelManager?.layer
        labelLayer?.let { layer ->
            val locationLabel = layer.addLabel(
                LabelOptions.from(position).setRank(10)
                    .setStyles(
                        LabelStyles.from(
                            LabelStyle.from(R.drawable.current_location)
                                .setAnchorPoint(0.5f, 0.5f)
                        )
                    )
                    .setTransform(TransformMethod.AbsoluteRotation_Decal)
            )

            val headingLabel = layer.addLabel(
                LabelOptions.from(position).setRank(9)
                    .setStyles(
                        LabelStyles.from(
                            LabelStyle.from(R.drawable.red_direction_area)
                                .setAnchorPoint(0.5f, 1.0f)
                        )
                    )
                    .setTransform(TransformMethod.AbsoluteRotation_Decal)
            )

            // ✅ headingLabel이 locationLabel과 함께 움직이도록 설정
            locationLabel.addSharePosition(headingLabel)

            // ✅ circle wave를 위한 Polygon 생성
            val circleWavePolygon = kakaoMap.shapeManager?.layer?.addPolygon(
                PolygonOptions.from("circlePolygon")
                    .setVisible(true)
                    .setDotPoints(DotPoints.fromCircle(position, 1.0f))
                    .setStylesSet(
                        PolygonStylesSet.from(
                            PolygonStyles.from(Color.parseColor("#f55d44"))
                        )
                    )
            )

            // ✅ circleWavePolygon이 현재 위치 Label과 함께 움직이도록 설정
            locationLabel.addShareTransform(circleWavePolygon)
        }
    }

    // ✅ 위치 권한 체크 함수
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // ✅ 위치 권한 요청 함수
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}

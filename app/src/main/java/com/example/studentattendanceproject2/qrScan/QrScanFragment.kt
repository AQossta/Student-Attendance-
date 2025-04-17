package com.example.studentattendanceproject2.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.studentattendanceproject2.data.ServiceBuilder
import com.example.studentattendanceproject2.data.request.QrScanRequest
import com.example.studentattendanceproject2.databinding.FragmentQrScanBinding
import com.example.studentattendanceproject2.login.AuthViewModel
import com.example.studentattendanceproject2.service.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Log

class QrScanFragment : Fragment() {

    private var _binding: FragmentQrScanBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val apiService = ServiceBuilder.buildService(ApiService::class.java)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var isScanning = true
    private var scheduleId: Long? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Требуется разрешение на камеру", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Разрешение предоставлено
        } else {
            Toast.makeText(requireContext(), "Требуется разрешение на геолокацию", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermission()
        checkLocationPermission()

        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.attendButton.setOnClickListener {
            scheduleId?.let { id ->
                sendAttendanceRequest(id)
            } ?: Toast.makeText(requireContext(), "QR-код не распознан", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (isScanning) {
                            processImageForQrCode(imageProxy) { qrContent ->
                                if (qrContent != null) {
                                    try {
                                        Log.d("QRScan", "Before parsing to Long: qrContent='$qrContent'")
                                        scheduleId = qrContent.toLong()
                                        Log.d("QRScan", "After parsing to Long: scheduleId=$scheduleId")
                                        isScanning = false
                                        lifecycleScope.launch {
                                            binding.tvHint.text = "QR-код распознан: $scheduleId"
                                            binding.attendButton.visibility = View.VISIBLE
                                        }
                                    } catch (e: NumberFormatException) {
                                        Log.e("QRScan", "Invalid QR format: $qrContent, error: ${e.message}")
                                        lifecycleScope.launch {
                                            Toast.makeText(
                                                requireContext(),
                                                "Неверный формат QR-кода",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        scheduleId = null
                                    }
                                } else {
                                    Log.d("QRScan", "No valid QR code detected")
                                }
                            }
                        }
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("QRScan", "Camera binding error: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка камеры: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageForQrCode(
        imageProxy: androidx.camera.core.ImageProxy,
        onQrCodeDetected: (String?) -> Unit
    ) {
        try {
            val bitmap = imageProxy.toBitmap() ?: run {
                Log.d("QRScan", "Bitmap conversion failed")
                onQrCodeDetected(null)
                return
            }

            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()

            val result = reader.decode(binaryBitmap)
            val qrContent = result.text

            Log.d("QRScan", "Raw QR content: '$qrContent'")

            // Проверка, что содержимое состоит только из цифр
            if (qrContent.isNotEmpty() && qrContent.all { it.isDigit() }) {
                Log.d("QRScan", "Valid QR content (scheduleId): $qrContent")
                onQrCodeDetected(qrContent)
            } else {
                Log.d("QRScan", "Invalid QR content (not numeric): $qrContent")
                onQrCodeDetected(null)
            }
        } catch (e: Exception) {
            Log.e("QRScan", "Error decoding QR code: ${e.message}")
            onQrCodeDetected(null)
        }
    }

    private fun sendAttendanceRequest(scheduleId: Long) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.attendButton.isEnabled = false

                val userId = authViewModel.userData.value?.id
                val token = authViewModel.userData.value?.accessToken

                if (userId == null || token.isNullOrEmpty()) {
                    Log.e("QRScan", "User data missing: userId=$userId, token=$token")
                    Toast.makeText(requireContext(), "Ошибка: данные пользователя отсутствуют", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val location = getLastKnownLocation()
                val latitude = location?.latitude ?: 0.0
                val longitude = location?.longitude ?: 0.0

                val request = QrScanRequest(
                    userId = userId,
                    scheduleId = scheduleId,
                    scanType = "IN",
                    latitude = latitude,
                    longitude = longitude
                )

                Log.d("QRScan", "Sending attendance request: scheduleId=$scheduleId, userId=$userId")
                val response = apiService.scanQrCode(request, token)
                Log.d("QRScan", "Attendance response: ${response.message}")
                Toast.makeText(
                    requireContext(),
                    "Успешно отмечено: ${response.message}",
                    Toast.LENGTH_LONG
                ).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Log.e("QRScan", "Attendance request failed: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.attendButton.isEnabled = true
            }
        }
    }

    private suspend fun getLastKnownLocation(): android.location.Location? {
        return try {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = fusedLocationProviderClient.lastLocation.await()
                if (location == null) {
                    Log.w("QRScan", "Location unavailable")
                    Toast.makeText(requireContext(), "Местоположение недоступно", Toast.LENGTH_LONG).show()
                }
                location
            } else {
                Log.w("QRScan", "Location permission not granted")
                Toast.makeText(requireContext(), "Разрешение на местоположение не предоставлено", Toast.LENGTH_LONG).show()
                null
            }
        } catch (e: Exception) {
            Log.e("QRScan", "Error getting location: ${e.message}")
            Toast.makeText(requireContext(), "Ошибка получения местоположения: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}

fun androidx.camera.core.ImageProxy.toBitmap(): android.graphics.Bitmap? {
    try {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = android.graphics.YuvImage(nv21, android.graphics.ImageFormat.NV21, width, height, null)
        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 90, out)
        val imageBytes = out.toByteArray()
        return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        Log.e("QRScan", "Error converting image to bitmap: ${e.message}")
        return null
    }
}
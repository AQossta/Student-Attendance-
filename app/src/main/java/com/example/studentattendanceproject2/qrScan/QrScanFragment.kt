package com.example.studentattendanceproject2.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrScanFragment : Fragment() {

    private var _binding: FragmentQrScanBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val apiService = ServiceBuilder.buildService(ApiService::class.java)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isScanning = true
    private var qrCodeContent: String? = null
    private lateinit var attendButton: Button

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
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Требуется разрешение на геолокацию", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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

        attendButton = Button(requireContext()).apply {
            text = "Сабақта қатысу"
            visibility = View.GONE
            setOnClickListener {
                qrCodeContent?.let { code ->
                    sendAttendanceRequest(code)
                } ?: Toast.makeText(requireContext(), "QR-код не распознан", Toast.LENGTH_SHORT).show()
            }
        }
        binding.root.addView(attendButton, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
            setMargins(0, 0, 0, 100)
        })
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
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже есть
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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

            // Анализатор изображения для сканирования QR
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (isScanning) {
                            processImageForQrCode(imageProxy) { qrContent ->
                                if (qrContent != null) {
                                    isScanning = false
                                    qrCodeContent = qrContent
                                    lifecycleScope.launch {
                                        binding.tvHint.text = "QR-код распознан"
                                        attendButton.visibility = View.VISIBLE
                                    }
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
                Toast.makeText(requireContext(), "Ошибка камеры: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageForQrCode(
        imageProxy: androidx.camera.core.ImageProxy,
        onQrCodeDetected: (String?) -> Unit
    ) {
        try {
            // Конвертируем ImageProxy в Bitmap
            val bitmap = imageProxy.toBitmap() ?: run {
                onQrCodeDetected(null)
                return
            }

            // Извлекаем данные изображения для ZXing
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()

            val result = reader.decode(binaryBitmap)
            onQrCodeDetected(result.text)
        } catch (e: Exception) {
            onQrCodeDetected(null)
        }
    }

    private fun sendAttendanceRequest(qrCode: String) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                attendButton.isEnabled = false

                val userId = authViewModel.userData.value?.id
                    ?: run {
                        Toast.makeText(requireContext(), "User ID отсутствует", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                val token = authViewModel.userData.value?.accessToken
                    ?: run {
                        Toast.makeText(requireContext(), "Токен отсутствует", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                val location = getLastKnownLocation()
                val latitude = location?.latitude ?: 0.0
                val longitude = location?.longitude ?: 0.0

                val request = QrScanRequest(
                    userId = userId,
                    code = qrCode,
                    scanType = "QR_CODE",
                    latitude = latitude,
                    longitude = longitude
                )

                val response = apiService.scanQrCode(request)
                Toast.makeText(
                    requireContext(),
                    "Успешно отмечено: ${response.message}",
                    Toast.LENGTH_LONG
                ).show()
                parentFragmentManager.popBackStack()
            } catch (e: HttpException) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка сервера: ${e.code()}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка сети: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                attendButton.isEnabled = true
            }
        }
    }

    private suspend fun getLastKnownLocation(): android.location.Location? {
        return try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val location = fusedLocationClient.lastLocation.await()
                if (location == null) {
                    Toast.makeText(
                        requireContext(),
                        "Местоположение недоступно. Включите GPS или проверьте настройки.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                location
            } else {
                Toast.makeText(
                    requireContext(),
                    "Разрешение на местоположение не предоставлено.",
                    Toast.LENGTH_LONG
                ).show()
                null
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Ошибка получения местоположения: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}

// Вспомогательная функция для конвертации ImageProxy в Bitmap
fun androidx.camera.core.ImageProxy.toBitmap(): android.graphics.Bitmap? {
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
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
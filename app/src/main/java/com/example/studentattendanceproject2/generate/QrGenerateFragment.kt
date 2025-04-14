package com.example.studentattendanceproject2.generate

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.studentattendanceproject2.data.response.ScheduleTeacherResponse
import com.example.studentattendanceproject2.data.ServiceBuilder
import com.example.studentattendanceproject2.login.AuthViewModel
import com.example.studentattendanceproject2.service.ApiService
import com.example.studentattendanceproject2.databinding.FragmentQrGenerateBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class QrGenerateFragment : Fragment() {

    private var _binding: FragmentQrGenerateBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val apiService = ServiceBuilder.buildService(ApiService::class.java)
    private var scheduleData: ScheduleTeacherResponse? = null
    private var qrGenerationJob: Job? = null
    private var isGenerating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleData = arguments?.getSerializable("scheduleData") as? ScheduleTeacherResponse
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrGenerateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Отображаем данные расписания
        scheduleData?.let { schedule ->
            binding.textViewSubject.text = schedule.subject
            binding.textViewGroup.text = "Группа: ${schedule.groupName}"
            binding.textViewTime.text = "${schedule.startTime.split("T")[1].substring(0, 5)} - ${schedule.endTime.split("T")[1].substring(0, 5)}"
        } ?: run {
            Toast.makeText(requireContext(), "Данные расписания отсутствуют", Toast.LENGTH_SHORT).show()
        }

        binding.btnGenerate.setOnClickListener {
            scheduleData?.let { schedule ->
                authViewModel.userData.value?.accessToken?.let { token ->
                    if (!isGenerating) {
                        startQrGeneration(schedule.id, token)
                        binding.btnGenerate.text = "Остановить"
                        isGenerating = true
                    } else {
                        stopQrGeneration()
                        binding.btnGenerate.text = "Генерировать"
                        isGenerating = false
                    }
                } ?: Toast.makeText(requireContext(), "Токен отсутствует", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(requireContext(), "Данные расписания отсутствуют", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startQrGeneration(scheduleId: Long, authToken: String) {
        qrGenerationJob = lifecycleScope.launch {
            while (isActive) {
                generateQrCode(scheduleId, authToken)
                delay(60_000) // 1 минута
            }
        }
    }

    private fun stopQrGeneration() {
        qrGenerationJob?.cancel()
        qrGenerationJob = null
    }

    private fun generateQrCode(scheduleId: Long, authToken: String) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.qrCodeImageView.visibility = View.GONE
                println("Отправка запроса: POST /api/v1/teacher/qr/generate?scheduleId=$scheduleId с токеном: $authToken")
                val response = apiService.generateQrCode(scheduleId, authToken)
                println("Ответ: code=${response.body?.qrCode}, message=${response.message}, body=${response.body}")
                if (response.body != null) {
                    val qrBody = response.body
                    val qrBitmap = generateQrBitmap(qrBody.qrCode)
                    binding.qrCodeImageView.setImageBitmap(qrBitmap)
                    binding.qrCodeImageView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "QR-код сгенерирован", Toast.LENGTH_SHORT).show()
                } else {
                    println("Ошибка: пустой ответ от сервера, message=${response.message}")
                    Toast.makeText(requireContext(), "Ошибка: ${response.message ?: "Пустой ответ сервера"}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                println("HTTP ошибка: ${e.code()} - ${e.message()} - ${e.response()?.errorBody()?.string()}")
                Toast.makeText(requireContext(), "Ошибка сервера: ${e.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                println("Ошибка сети: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                println("Неизвестная ошибка: ${e.message}")
                Toast.makeText(requireContext(), "Неизвестная ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun generateQrBitmap(qrCode: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(qrCode, BarcodeFormat.QR_CODE, 300, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun onDestroyView() {
        stopQrGeneration() // Останавливаем генерацию при уничтожении фрагмента
        super.onDestroyView()
        _binding = null
    }
}
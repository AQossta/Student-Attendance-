package com.example.studentattendanceproject2.generate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.util.Base64
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.R

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
            binding.textViewTime.text =
                "${schedule.startTime.split("T")[1].substring(0, 5)} - ${schedule.endTime.split("T")[1].substring(0, 5)}"
        } ?: run {
            Toast.makeText(requireContext(), "Данные расписания отсутствуют", Toast.LENGTH_SHORT).show()
        }

        binding.toolbarQrGenerate.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.action_qrGenerateFragment_to_homeFragment)
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
                delay(60_000) // каждые 60 секунд
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
                println("Отправка запроса: scheduleId=$scheduleId с токеном: $authToken")

                val response = apiService.generateQrCode(scheduleId, authToken)
                val body = response.body

                if (body != null) {
                    val qrCode = body.qrCode
                    val qrBitmap: Bitmap = try {
                        // Пробуем декодировать Base64
                        val base64Data = if (qrCode.contains(",")) qrCode.split(",")[1] else qrCode
                        val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    } catch (e: IllegalArgumentException) {
                        // Если не удалось, генерируем QR-код из текста
                        generateQrBitmap(qrCode)
                    }

                    binding.qrCodeImageView.setImageBitmap(qrBitmap)
                    binding.qrCodeImageView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "QR-код сгенерирован", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка: пустой ответ от сервера", Toast.LENGTH_SHORT).show()
                }

            } catch (e: HttpException) {
                println("HTTP ошибка: ${e.code()} - ${e.message()}")
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
        val size = 512
        val bitMatrix = writer.encode(qrCode, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun onDestroyView() {
        stopQrGeneration()
        super.onDestroyView()
        _binding = null
    }
}

package com.example.studentattendanceproject2.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentattendanceproject2.Data.ServiceBuilder
import com.example.studentattendanceproject2.Login.AuthViewModel
import com.example.studentattendanceproject2.Service.ApiService
import com.example.studentattendanceproject2.databinding.FragmentHomeBinding
import com.example.studentattendanceproject2.provideNavigationHost
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScheduleAdapter
    private val apiService = ServiceBuilder.buildService(ApiService::class.java)
    private val authViewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.rcMainCategories1.layoutManager = LinearLayoutManager(requireContext())
        adapter = ScheduleAdapter(emptyList())
        binding.rcMainCategories1.adapter = adapter

        authViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                for(item in it.roles){
                    if(item.equals("teacher")) {
                        println("Пользователь является преподавателем")
                        fetchTeacherScheduleData(it.id, it.accessToken)
                    } else if(item.equals("student")) {
                        fetchScheduleData(it.groupId, it.accessToken)
                    } else {
                        throw Exception("Неизвестная роль* пользователя: $item")
                    }
                }
            } ?: run {
                println("Ошибка: пользовательские данные отсутствуют")
            }
        }

        return binding.root
    }

    private fun fetchTeacherScheduleData(teacherId: Long, accessToken: String){
        lifecycleScope.launch {
            try {
                println("Используемый токен: $accessToken") // Вывод токена в консоль
                println("Запрос расписания для преподавателя $teacherId отправлен...")

                val response = apiService.scheduleTeacher(teacherId, accessToken)

                if (response.body != null) {
                    println("Ответ получен: ${response.body}")
                    adapter.updateTeacherData(response.body)
                } else {
                    println("Ошибка: ${response.message ?: "Пустой ответ сервера"}")
                }

                } catch (e: IOException) {
                println("Ошибка сети: ${e.message}")
                } catch (e: HttpException) {
                println("Ошибка сервера: ${e.code()} - ${e.message()}")
                } catch (e: Exception) {
                    println("Неизвестная ошибка: ${e.message}")
            }
        }
    }

    private fun fetchScheduleData(groupId: Long, accessToken: String) {
        lifecycleScope.launch {
            try {
                println("Используемый токен: $accessToken") // Вывод токена в консоль
                println("Запрос расписания для группы $groupId отправлен...")

                val response = apiService.scheduleGroup(groupId, accessToken)
                println("Сырой ответ: $response")

                if (response.body != null) {
                    println("Ответ получен: ${response.body}")
                    adapter.updateData(response.body)
                } else {
                    println("Ошибка: ${response.message ?: "Пустой ответ сервера"}")
                }

            } catch (e: IOException) {
                println("Ошибка сети: ${e.message}")
            } catch (e: HttpException) {
                println("Ошибка сервера: ${e.code()} - ${e.message()}")
            } catch (e: Exception) {
                println("Неизвестная ошибка: ${e.message}")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        provideNavigationHost()?.apply {
            setNavigationVisibility(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
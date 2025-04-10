package com.example.studentattendanceproject2.Profile.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.Data.SharedProvider
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.Service.ApiService
import com.example.studentattendanceproject2.databinding.ConfirmationOfExitBinding
import com.example.studentattendanceproject2.provideNavigationHost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ConfirmationOfExit : BottomSheetDialogFragment() {
    private lateinit var binding: ConfirmationOfExitBinding
    private val apiService = ApiService.create() // Инициализация API

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConfirmationOfExitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        provideNavigationHost()?.apply {
            setNavigationVisibility(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        provideNavigationHost()?.apply {
            setNavigationVisibility(false)
        }

        binding.btnUserYesExit.setOnClickListener {
            logoutUser()
        }

        binding.btnUserNoExit.setOnClickListener {
            dismiss() // Закрываем BottomSheet
        }
    }

    private fun logoutUser() {
        val token = SharedProvider(requireContext()).getToken() // Получаем токен

        if (token.isNullOrEmpty()) {
            logoutLocally()
            return
        }

        // Отправляем запрос на сервер
        lifecycleScope.launch {
            try {
                val response = apiService.logout("Bearer $token")
                if (response.body != null) {
                    logoutLocally()
                } else {
                    Toast.makeText(requireContext(), "Ошибка: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Ошибка сервера: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logoutLocally() {
        // Очищаем данные пользователя
        SharedProvider(requireContext()).clearUserData()

        // Переход на экран входа
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }
}
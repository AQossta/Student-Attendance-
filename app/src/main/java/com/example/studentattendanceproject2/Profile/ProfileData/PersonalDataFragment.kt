package com.example.studentattendanceproject2.Profile.ProfileData

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.Login.AuthViewModel
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.databinding.FragmentPersonalDataBinding
import com.example.studentattendanceproject2.provideNavigationHost

class PersonalDataFragment : Fragment() {
    private lateinit var binding: FragmentPersonalDataBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalDataBinding.inflate(layoutInflater, container, false)
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
        binding.toolbarProfile.btnBackProfile.setOnClickListener{
            findNavController().popBackStack()
        }

        authViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                binding.tvNameUserData.text = it.name
                binding.tvEmailUserData.text = it.email
                binding.tvNumberUser.text = it.phoneNumber
                binding.tvStatusUser.text = it.roles.toString()
                binding.tvGroupUser.text = it.groupName.toString()

            } ?: run {
                println("Ошибка: пользовательские данные отсутствуют")
            }
        }
    }
}
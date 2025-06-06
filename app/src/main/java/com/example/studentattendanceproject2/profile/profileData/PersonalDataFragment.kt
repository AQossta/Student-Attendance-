package com.example.studentattendanceproject2.profile.profileData

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.login.AuthViewModel
import com.example.studentattendanceproject2.databinding.FragmentPersonalDataBinding
import com.example.studentattendanceproject2.provideNavigationHost

class PersonalDataFragment : Fragment() {

    private lateinit var binding: FragmentPersonalDataBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PersonalDataFragment", "onCreateView called")
        binding = FragmentPersonalDataBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("PersonalDataFragment", "onStart called")
        provideNavigationHost()?.apply {
            setNavigationVisibility(false)
            Log.d("PersonalDataFragment", "Navigation visibility set to false")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PersonalDataFragment", "onViewCreated called")

        provideNavigationHost()?.apply {
            setNavigationVisibility(false)
            Log.d("PersonalDataFragment", "Navigation visibility set to false")
        }

        binding.toolbarProfile.btnBackProfile.setOnClickListener {
            Log.d("PersonalDataFragment", "Back button clicked")
            findNavController().navigate(R.id.action_personalDataFragment_to_profileFragment)
        }

        authViewModel.userData.observe(viewLifecycleOwner) { userData ->
            Log.d("PersonalDataFragment", "userData observed: $userData")
            userData?.let {
                binding.tvNameUserData.text = it.name
                binding.tvEmailUserData.text = it.email
                binding.tvNumberUser.text = it.phoneNumber
                binding.tvStatusUser.text = it.roles.toString()
                binding.tvGroupUser.text = it.groupName?.toString()
                Log.d("PersonalDataFragment", "User data set to views")
            } ?: run {
                Log.e("PersonalDataFragment", "Error: userData is null")
            }
        }

    }
}

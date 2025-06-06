package com.example.studentattendanceproject2.profile

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.data.OnLanguageSelectedListener
import com.example.studentattendanceproject2.data.SharedProvider
import com.example.studentattendanceproject2.login.AuthViewModel
import com.example.studentattendanceproject2.profile.bottomSheet.ConfirmationOfExit
import com.example.studentattendanceproject2.profile.bottomSheet.SelectLanguageBottomSheet
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.databinding.FragmentProfileBinding
import com.example.studentattendanceproject2.provideNavigationHost
import java.util.Locale

class ProfileFragment : Fragment(), OnLanguageSelectedListener {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        provideNavigationHost()?.apply {
            setNavigationVisibility(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        provideNavigationHost()?.apply {
            setNavigationVisibility(true)
        }
        systemLanguage()

        authViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                binding.tvEmailUser.text = it.email
                binding.profileTitle.text = it.name
            } ?: run {
                println("Ошибка: пользовательские данные отсутствуют")
            }
        }

        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            transaction.setReorderingAllowed(false)
        }
        transaction.detach(this).attach(this).commit()

        viewModel.language.observe(viewLifecycleOwner) {
            binding.tvSelectedLanguage.text = it
        }

        binding.btnChangeLanguage.setOnClickListener {
            val bottomSheet = SelectLanguageBottomSheet()
            bottomSheet.setOnLanguageSelectedListener(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.toolbarProfile.btnExit.setOnClickListener {
            val logoutBottomSheet = ConfirmationOfExit()
            logoutBottomSheet.show(parentFragmentManager, "LogoutBottomSheet")
        }

        binding.btnUserDataEdit.setOnClickListener {
            Log.d("ProfileFragment", "btnUserDataEdit clicked")
            findNavController().navigate(R.id.action_profileFragment_to_personalDataFragment)
        }
    }

    override fun onLanguageSelected(language: String) {
        viewModel.selectLanguage(language)
    }

    private fun systemLanguage() {
        when (SharedProvider(requireContext()).getLanguage()) {
            "kk" -> {
                val local = Locale("kk")
                Locale.setDefault(local)
                val config = resources.configuration
                config.setLocale(local)
                requireContext().resources.updateConfiguration(
                    config,
                    requireContext().resources.displayMetrics
                )
                binding.tvSelectedLanguage.text = "Қазақша"
            }

            "ru" -> {
                val local = Locale("ru")
                Locale.setDefault(local)
                val config = resources.configuration
                config.setLocale(local)
                requireContext().resources.updateConfiguration(
                    config,
                    requireContext().resources.displayMetrics
                )
                binding.tvSelectedLanguage.text = "Русский"
            }

            "en" -> {
                val local = Locale("en")
                Locale.setDefault(local)
                val config = resources.configuration
                config.setLocale(local)
                requireContext().resources.updateConfiguration(
                    config,
                    requireContext().resources.displayMetrics
                )
                binding.tvSelectedLanguage.text = "English"
            }

            else -> {
                val local = Locale("kk")
                Locale.setDefault(local)
                val config = resources.configuration
                config.setLocale(local)
                requireContext().resources.updateConfiguration(
                    config,
                    requireContext().resources.displayMetrics
                )
                binding.tvSelectedLanguage.text = "Қазақша"
            }
        }
    }
}
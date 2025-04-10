package com.example.studentattendanceproject2.Login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.databinding.FragmentLoginBinding
import com.example.studentattendanceproject2.provideNavigationHost

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        provideNavigationHost()?.apply {
            setNavigationVisibility(false)
        }

        viewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                val loginData = it.body  // Теперь `body` — это LoginResponse
                Toast.makeText(requireContext(), "Сәтті кіру!", Toast.LENGTH_SHORT).show()
                authViewModel.setUserData(loginData)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            showError(it)
        }

        binding.btnShowPassword.setOnClickListener {
            togglePasswordVisibility()
        }
        binding.btnLogin.setOnClickListener{
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            val isValidEmail = emailValidation(email)
            val isValidPassword = validationPassword(password)

            if(isValidEmail && isValidPassword){
                viewModel.loginUser(email, password)
            }else{
                validationEmail(isValidEmail)
            }

        }
    }

    fun togglePasswordVisibility() {
        val passwordEditText = binding.editTextPassword
        passwordEditText.transformationMethod = if (passwordEditText.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
            PasswordTransformationMethod.getInstance()
        } else {
            HideReturnsTransformationMethod.getInstance()
        }
    }

    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    fun emailValidation(email: String): Boolean {
        return email.trim().matches(emailPattern.toRegex())
    }
    fun validationEmail(isValid: Boolean) {
        if(isValid){
            binding.tvErrorTextEmail.text = ""
            binding.tvErrorTextEmail.visibility = View.GONE
            binding.editTextEmail.setBackgroundResource(R.drawable.background_edittext_focus_action_12dp)
        }else{
            binding.tvErrorTextEmail.text = "Қате формат"
            binding.tvErrorTextEmail.visibility = View.VISIBLE
            binding.editTextEmail.setBackgroundResource(R.drawable.background_edittext_12dp_error)
        }
    }

    fun validationPassword(password: String): Boolean {
        return if(password.length < 5){
            binding.tvErrorTextPasswordAndServer.text = "Құпия сөз ұзындығы 6 таңбадан кем емес"
            binding.tvErrorTextPasswordAndServer.visibility = View.VISIBLE
            binding.editTextPassword.setBackgroundResource(R.drawable.background_edittext_12dp_error)
            false
        }else{
            binding.tvErrorTextPasswordAndServer.visibility = View.GONE
            binding.editTextPassword.setBackgroundResource(R.drawable.background_edittext_focus_action_12dp)
            true
        }
    }

    fun showError(message: String) {
        binding.tvErrorTextPasswordAndServer.text = "Сізде email немесе құпия сөз дұрыс емес"
        binding.tvErrorTextPasswordAndServer.visibility = View.VISIBLE
    }
}
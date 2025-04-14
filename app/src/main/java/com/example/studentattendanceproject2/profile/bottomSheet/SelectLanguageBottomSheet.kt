package com.example.studentattendanceproject2.profile.bottomSheet

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.studentattendanceproject2.data.OnLanguageSelectedListener
import com.example.studentattendanceproject2.data.SharedProvider
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.databinding.BottomsheetSelectLanguageBinding
import com.example.studentattendanceproject2.provideNavigationHost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class SelectLanguageBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetSelectLanguageBinding
    private var languageSelectedListener: OnLanguageSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetSelectLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setOnLanguageSelectedListener(listener: OnLanguageSelectedListener) {
        languageSelectedListener = listener
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
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val defaultLanguage: String = SharedProvider(requireContext()).getLanguage()
        when (defaultLanguage) {
            "kk" -> {
                selectedIcon(true, false, false)
            }

            "en" -> {
                selectedIcon(false, true, false)
            }

            "ru" -> {
                selectedIcon(false, false, true)
            }
        }

        binding.apply {
            btnSelectKazakh.setOnClickListener {
                selectedIcon(true, false, false)
                changeLanguage("Kazakh")
            }
            btnSelectEnglish.setOnClickListener {
                selectedIcon(false, true, false)
                changeLanguage("English")
            }
            btnSelectRussia.setOnClickListener {
                selectedIcon(false, false, true)
                changeLanguage("Russian")
            }
        }
    }

    fun changeLanguage(language: String) {
        when (language) {
            "Kazakh" -> {
                systemLanguageChange("kk")
                languageSelectedListener?.onLanguageSelected("Қазақша")
            }

            "English" -> {
                systemLanguageChange("en")
                languageSelectedListener?.onLanguageSelected("English")
            }

            "Russian" -> {
                systemLanguageChange("ru")
                languageSelectedListener?.onLanguageSelected("Русский")
            }
        }
    }

    fun systemLanguageChange(codeLanguage: String) {
        SharedProvider(requireContext()).saveLanguage(codeLanguage)
        val local = Locale(codeLanguage)
        Locale.setDefault(local)
        val config = Configuration()
        config.setLocale(local)
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )
        findNavController().navigate(
            R.id.profileFragment,
            arguments,
            NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build()
        )
    }

    fun selectedIcon(iconKazakh: Boolean, iconEnglish: Boolean, iconRussia: Boolean) {
        binding.apply {
            if (iconKazakh) {
                imgIconBtnSelectKazakh.visibility = View.VISIBLE
            } else {
                imgIconBtnSelectKazakh.visibility = View.GONE
            }
            if (iconEnglish) {
                imgIconBtnSelectEnglish.visibility = View.VISIBLE
            } else {
                imgIconBtnSelectEnglish.visibility = View.GONE
            }
            if (iconRussia) {
                imgIconBtnSelectRussia.visibility = View.VISIBLE
            } else {
                imgIconBtnSelectRussia.visibility = View.GONE
            }
        }
    }
}
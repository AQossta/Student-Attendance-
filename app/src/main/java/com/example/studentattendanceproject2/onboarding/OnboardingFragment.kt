package com.example.studentattendanceproject2.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.studentattendanceproject2.data.OnboardingInfoList
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.databinding.FragmentOnboardingBinding
import com.example.studentattendanceproject2.provideNavigationHost

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        provideNavigationHost()?.apply {
            setNavigationVisibility(false)
        }

        val adapter = OnboardingAdapter()
        adapter.submitList(OnboardingInfoList.onboardingModelList)
        binding.viewPager2OnboardingFragment.adapter = adapter

        val viewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == OnboardingInfoList.onboardingModelList.size - 1) {
                    binding.btnNextOnboardingFragment.visibility = View.VISIBLE
                } else {
                    binding.btnNextOnboardingFragment.visibility = View.INVISIBLE
                }
            }
        }

        binding.viewPager2OnboardingFragment.registerOnPageChangeCallback(viewPagerCallback)


        binding.btnNextOnboardingFragment.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }

    }
}
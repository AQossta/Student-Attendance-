package com.example.studentattendanceproject2.qrScan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.studentattendanceproject2.R
import com.example.studentattendanceproject2.data.SharedProvider
import com.example.studentattendanceproject2.data.request.ScanRequest
import com.example.studentattendanceproject2.databinding.FragmentQrScanConfirmationBinding
import com.example.studentattendanceproject2.login.AuthViewModel
import com.example.studentattendanceproject2.service.ApiService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QrScanConfirmationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentQrScanConfirmationBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedProvider: SharedProvider
    private val apiService = ApiService.create()
    private var scannedData: String? = null
    private var scheduleId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannedData = arguments?.getString("scannedData")
        scheduleId = scannedData?.toLongOrNull()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrScanConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        val userId = authViewModel.userData.value?.id
        sharedProvider = SharedProvider(requireContext())
        val token = sharedProvider.getToken()

        binding.tvSelect.text = "Сабақ үшін сканерлеу түрін таңдаңыз: #${scannedData ?: "?"}"

        binding.btnUserYesLesson.setOnClickListener {
            handleScanConfirm("IN")
        }

        binding.btnUserNoLesson.setOnClickListener {
            Toast.makeText(requireContext(), "OUT батырмасы басылды", Toast.LENGTH_SHORT).show()
            handleScanConfirm("OUT")
        }
    }

    private fun handleScanConfirm(scanType: String) {
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0

        val user = authViewModel.userData.value
        val userId = user?.id
        val token = user?.accessToken
        val scheduleId = this.scheduleId

        if (userId == null || scheduleId == null || token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Мәліметтер толық емес", Toast.LENGTH_LONG).show()
            dismiss()
            return
        }

        val request = ScanRequest(
            userId = userId,
            scheduleId = scheduleId,
            scanType = scanType,
            latitude = latitude,
            longitude = longitude
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.scanQrCode(request, token)
                }

                if (response.message == null) {
                    Toast.makeText(requireContext(), "Сканерлеу сәтті!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Қате: ${response.message}", Toast.LENGTH_LONG).show()
                }
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Қате: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                dismiss()
            }
        }
    }



    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null

            // QR скан қайта жұмыс істеуі үшін флагты false қыламыз
            (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments
                ?.firstOrNull { it is QrScanFragment } as? QrScanFragment)?.resetScanner()
        }



        companion object {
        const val TAG = "QrScanConfirmationFragment"
            fun newInstance(scannedData: String, latitude: Double, longitude: Double): QrScanConfirmationFragment {
                val fragment = QrScanConfirmationFragment()
                val args = Bundle().apply {
                    putString("scannedData", scannedData)
                    putDouble("latitude", latitude)
                    putDouble("longitude", longitude)
                }
                fragment.arguments = args
                return fragment
            }

        }
}

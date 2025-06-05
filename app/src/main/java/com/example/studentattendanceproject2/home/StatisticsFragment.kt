package com.example.studentattendanceproject2.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentattendanceproject2.data.SharedProvider
import com.example.studentattendanceproject2.data.response.AttendanceStats
import com.example.studentattendanceproject2.data.response.StudentAttendance
import com.example.studentattendanceproject2.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatisticsViewModel
    private lateinit var sharedProvider: SharedProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        sharedProvider = SharedProvider(requireContext())

        val scheduleId = requireArguments().getInt("scheduleId")
        sharedProvider.getToken()?.let { token ->
            viewModel.loadStats(scheduleId, token)
        }

        viewModel.statsResponse.observe(viewLifecycleOwner) { response ->
            response?.body?.let { populateData(it) }
        }

        binding.btnBackStats.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun populateData(data: AttendanceStats) = with(binding) {
        // Сабақ туралы ақпарат
        textTvLesson.text = data.scheduleDTO.subject
        tvTimeText.text = "${data.scheduleDTO.startTime} - ${data.scheduleDTO.endTime}"
        tvGroupText.text = data.scheduleDTO.groupName
        tvTeacherText.text = data.scheduleDTO.teacherName

        // Статистика
        tvTotalStudents.text = data.totalCount.toString()
        tvPresentStudents.text = data.presentCount.toString()
        tvAttendancePercentage.text = "${data.statistic}%"
        tvMessageLabel.text = data.statistic.toString()


        val studentList = data.studentDTO.map {
            StudentAttendance(
                name = it.name,
                email = it.email,
                phoneNumber = it.phoneNumber,
                attendTime = it.attendTime,
                exitTime = it.exitTime,
                attendanceDuration = it.attendanceDuration,
                id = it.id,
                attend = it.attend
            )
        }

        // RecyclerView
        val adapter = StudentAdapter(studentList)
        recyclerViewClassList.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewClassList.adapter = adapter
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

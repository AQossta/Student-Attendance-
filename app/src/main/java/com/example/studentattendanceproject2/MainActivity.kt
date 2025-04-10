package com.example.studentattendanceproject2

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.studentattendanceproject2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationHostProvider {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding?.bottomNavBar?.itemIconTintList = null
        val navController = findNavController(R.id.nav_host_fragment)
        binding?.bottomNavBar?.setupWithNavController(navController)
    }

    override fun setNavigationVisibility(visible: Boolean) {
        if (visible) {
            binding?.bottomNavBar?.visibility = View.VISIBLE
        } else {
            binding?.bottomNavBar?.visibility = View.GONE
        }
    }
}
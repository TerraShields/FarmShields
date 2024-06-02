package com.gagak.farmshields

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gagak.farmshields.core.utils.helper.CustomBottomNavigationViewHelper
import com.gagak.farmshields.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frame_container) as NavHostFragment
        navController = navHostFragment.navController

        // Set up custom bottom navigation view
        CustomBottomNavigationViewHelper.setupBottomNavigationView(this, binding.bottomNav, navController)

        // Set up navigation with bottom nav
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.communityFragment,
                R.id.notificationsFragment -> {
                    showBottomNav()
                    CustomBottomNavigationViewHelper.updateSelectedItem(binding.bottomNav, destination.id)
                }
                R.id.mapsFragment -> hideBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    // Show navigation bottom
    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    // Hide navigation bottom
    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }
}

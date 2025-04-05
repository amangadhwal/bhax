package com.bhax.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bhax.app.databinding.ActivityMainBinding
import com.bhax.app.data.UserAgreementManager
import com.bhax.app.ui.UserAgreementDialog
import com.bhax.app.ui.theme.ThemeManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.config.Configuration
import android.preference.PreferenceManager
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var agreementManager: UserAgreementManager
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize managers
        themeManager = ThemeManager.getInstance(applicationContext)
        agreementManager = UserAgreementManager.getInstance(applicationContext)
        
        // Apply theme
        themeManager.setTheme(this)
        
        // Initialize OSMDroid
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        
        // Initialize UserAgreementManager
        agreementManager = UserAgreementManager.getInstance(applicationContext)
        
        // Set up view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)

        // Set up navigation item listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_editor -> {
                    if (!agreementManager.hasAgreed()) {
                        showAgreementDialog()
                        false
                    } else {
                        navController.navigate(R.id.navigation_editor)
                        true
                    }
                }
                else -> {
                    if (item.itemId == R.id.navigation_explorer) {
                        navController.navigate(R.id.navigation_explorer)
                    }
                    true
                }
            }
        }

        // Update edit tab subtitle
        updateEditTabSubtitle()
    }

    override fun onResume() {
        super.onResume()
        // Ensure configuration is reloaded
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        // Update edit tab subtitle
        updateEditTabSubtitle()
    }

    private fun showAgreementDialog() {
        UserAgreementDialog.show(
            this,
            onAgreeClick = { token ->
                updateEditTabSubtitle()
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_editor)
            },
            onExploreClick = {
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_explorer)
            }
        )
    }

    private fun updateEditTabSubtitle() {
        val editMenuItem = binding.bottomNavigation.menu.findItem(R.id.navigation_editor)
        editMenuItem?.let { menuItem ->
            if (agreementManager.hasAgreed()) {
                agreementManager.getToken()?.let { token ->
                    menuItem.title = getString(R.string.edit_title_with_token, token)
                }
            } else {
                menuItem.title = getString(R.string.edit_title_locked)
            }
        }
    }
}
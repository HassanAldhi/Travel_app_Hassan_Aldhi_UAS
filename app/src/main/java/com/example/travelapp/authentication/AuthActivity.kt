package com.example.travelapp.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp.admin.DashboardActivity
import com.example.travelapp.databinding.ActivityAuthBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    lateinit var viewPager2: ViewPager2
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Dashboard"
        supportActionBar?.hide()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)

        var isLoggedIn = prefManager.isLoggedIn()
        var userRole = prefManager.getRole()

        if (isLoggedIn){
            if (userRole == "admin"){
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }else if (userRole == "user") {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }

        viewPager2 = binding.viewPager

        with(binding) {
            viewPager.adapter = TabAdapter(this@AuthActivity)

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Login"
                    1 -> "Register"
                    else -> ""
                }
            }.attach()
        }

        // Aksi jika tab di klik langsung pada TabLayout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager2.setCurrentItem(it.position, true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }
}
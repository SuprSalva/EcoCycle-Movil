package com.example.appmovil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // Set Home as default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_history -> loadFragment(HistoryFragment())
                R.id.nav_placeholder -> false // Do nothing
                R.id.nav_rewards -> loadFragment(RewardsFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(this, QrActivity::class.java))
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}
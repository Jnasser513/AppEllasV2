package com.app.appellas.ui.views.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.app.appellas.R
import com.app.appellas.databinding.AdminBottomNavActivityBinding
import com.app.appellas.databinding.BottomNavActivityBinding

class AdminBottomNavActivity: AppCompatActivity() {

    private lateinit var mBinding: AdminBottomNavActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = AdminBottomNavActivityBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_admin_nav) as NavHostFragment

        val navController = navHostFragment.navController

        mBinding.bottomNavMenu.setOnClickListener {
            navController.navigate(R.id.adminBottomSheetMenuFragment)
        }

        mBinding.bottomNavProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment2)
        }
    }

}
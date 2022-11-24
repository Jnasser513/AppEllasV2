package com.app.appellas.ui.views.activitys

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.app.appellas.R
import com.app.appellas.databinding.BottomNavActivityBinding
import com.app.appellas.viewmodel.user.HomeViewModel

class BottomNavActivity: AppCompatActivity() {

    private lateinit var mBinding: BottomNavActivityBinding

    private var id: String? = null

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = BottomNavActivityBinding.inflate(layoutInflater)

        id = intent.extras?.getString("id", "")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        if(id != null) {
            Log.d("IDDATA", id.toString())
            viewModel.goAlertDetail(id.toString())
            viewModel.goAlertDetail.observe(this) {
                it?.let {
                    if(it) {
                        navController.navigate(R.id.alertDetailFragment)
                    }
                }
            }
        }

        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        mBinding.bottomNavMenu.setOnClickListener {
            navController.navigate(R.id.bottomSheetMenuFragment)
        }

        mBinding.bottomNavProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }

        mBinding.bottomNavActionAlert.setOnClickListener {
            navController.navigate(R.id.dialogAlertFragment)
        }
    }

}
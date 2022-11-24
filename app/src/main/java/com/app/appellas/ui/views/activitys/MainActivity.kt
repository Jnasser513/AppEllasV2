package com.app.appellas.ui.views.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.databinding.ActivityMainBinding
import com.app.appellas.viewmodel.user.AuthViewModel
import com.app.appellas.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.loginRepository)
    }

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = this@MainActivity
            }
        setContentView(mBinding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_login) as NavHostFragment

        val navController = navHostFragment.navController
    }
}
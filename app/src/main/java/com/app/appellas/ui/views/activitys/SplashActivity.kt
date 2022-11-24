package com.app.appellas.ui.views.activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.appellas.AppApplication
import com.app.appellas.databinding.SplashActivityBinding
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.user.AuthViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var mBinding: SplashActivityBinding

    private val userApp by lazy {
        application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.loginRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initTimer()
    }

    private fun initTimer() {
        Handler().postDelayed({
            authViewModel.userData.observe(this) {
                if (it.isEmpty()) {
                    startActivity(Intent(Intent(this, MainActivity::class.java)))
                    finish()
                } else {
                    if(it[0].rol == "usuario") {
                        val intent = (Intent(this, BottomNavActivity::class.java))
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else if(it[0].rol == "admin") {
                        val intent = (Intent(this, AdminBottomNavActivity::class.java))
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }
                finish()
            }
        }, 2000)
    }

}
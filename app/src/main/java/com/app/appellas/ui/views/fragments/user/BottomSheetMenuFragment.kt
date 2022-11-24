package com.app.appellas.ui.views.fragments.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.databinding.BottomSheetMenuFragmentBinding
import com.app.appellas.location.LocationService
import com.app.appellas.ui.views.activitys.MainActivity
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminAdvisorViewModel
import com.app.appellas.viewmodel.user.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetMenuFragment: BottomSheetDialogFragment() {

    private var mBinding: BottomSheetMenuFragmentBinding? = null
    private val binding get() = mBinding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.loginRepository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    activity?.onBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = BottomSheetMenuFragmentBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("appellas", Context.MODE_PRIVATE)!!

        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = NavHostFragment.findNavController(this)

        setUpListeners(navHost)
    }

    private fun setUpListeners(navHost: NavController) {
        binding.bottomSheetMenuContactContainer.setOnClickListener {
            navHost.navigate(R.id.contactsFragment)
        }

        binding.bottomSheetMenuHomeContainer.setOnClickListener {
            navHost.navigate(R.id.homeFragment)
        }

        binding.bottomSheetMenuBlogsContainer.setOnClickListener {
            navHost.navigate(R.id.blogsFragment)
        }

        binding.bottomSheetMenuAdvisoryContainer.setOnClickListener {
            navHost.navigate(R.id.advisoryFragment)
        }

        binding.bottomSheetMenuInformationContainer.setOnClickListener {
            navHost.navigate(R.id.informationFragment)
        }

        binding.bottomSheetMenuSignOutContainer.setOnClickListener {
            val intent = Intent(Intent(requireContext(), MainActivity::class.java))
            startActivity(intent)
            activity?.finish()
            authViewModel.signOut()
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }

            Intent(requireActivity().applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                requireActivity().startService(this)
            }
        }
    }

}
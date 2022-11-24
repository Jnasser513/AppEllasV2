package com.app.appellas.ui.views.fragments.admin

import android.content.Context
import android.content.Intent
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
import com.app.appellas.databinding.AdminBottomSheetMenuFragmentBinding
import com.app.appellas.databinding.BottomSheetMenuFragmentBinding
import com.app.appellas.ui.views.activitys.MainActivity
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.user.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdminBottomSheetMenuFragment: BottomSheetDialogFragment() {

    private var mBinding: AdminBottomSheetMenuFragmentBinding? = null
    private val binding get() = mBinding!!

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
                    dismiss()
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
        mBinding = AdminBottomSheetMenuFragmentBinding.inflate(inflater, container, false)

        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = NavHostFragment.findNavController(this)

        setUpListeners(navHost)
    }

    private fun setUpListeners(navHost: NavController) {
        binding.adminBottomSheetMenuHomeContainer.setOnClickListener {
            navHost.navigate(R.id.adminHomeFragment)
        }

        binding.adminBottomSheetMenuAccountsContainer.setOnClickListener {
            navHost.navigate(R.id.adminManageAccountFragment)
        }

        binding.adminBottomSheetMenuInformationContainer.setOnClickListener {
            navHost.navigate(R.id.adminInformationMapFragment)
        }

        binding.adminBottomSheetMenuAdvisoryContainer.setOnClickListener {
            navHost.navigate(R.id.adminAdvisoryFragment)
        }

        binding.bottomSheetMenuSignOutContainer.setOnClickListener {
            val intent = Intent(Intent(requireContext(), MainActivity::class.java))
            startActivity(intent)
            activity?.finish()
            authViewModel.signOut()
        }
    }

}
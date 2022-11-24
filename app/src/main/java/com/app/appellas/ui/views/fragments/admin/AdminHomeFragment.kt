package com.app.appellas.ui.views.fragments.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.appellas.R
import com.app.appellas.databinding.AdminHomeFragmentBinding

class AdminHomeFragment: Fragment() {

    private var mBinding: AdminHomeFragmentBinding? = null
    private val binding get() = mBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    activity?.finish()
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
        mBinding = AdminHomeFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()
    }

    private fun setNavigationListeners() {
        val navController = findNavController()

        binding.adminHomeManageAccounts.setOnClickListener {
            val direction = AdminHomeFragmentDirections
                .actionAdminHomeFragmentToAdminManageAccountFragment()
            navController.navigate(direction)
        }

        binding.adminHomeEmergencyContainer.setOnClickListener {
            val direction = AdminHomeFragmentDirections
                .actionAdminHomeFragmentToAdminInformationMapFragment()
            navController.navigate(direction)
        }

        binding.adminHomeManageAdvisoryContainer.setOnClickListener {
            val direction = AdminHomeFragmentDirections
                .actionAdminHomeFragmentToAdminAdvisoryFragment()
            navController.navigate(direction)
        }

    }

}
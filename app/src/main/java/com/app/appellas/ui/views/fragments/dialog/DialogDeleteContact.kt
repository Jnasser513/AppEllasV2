package com.app.appellas.ui.views.fragments.dialog

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.DialogAlertBinding
import com.app.appellas.databinding.DialogDeleteAccountBinding
import com.app.appellas.databinding.DialogDeleteAdvisorBinding
import com.app.appellas.databinding.DialogDeleteContactBinding
import com.app.appellas.ui.views.activitys.MainActivity
import com.app.appellas.viewmodel.user.LocationViewModel
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminAdvisorViewModel
import com.app.appellas.viewmodel.admin.AdminUserViewModel
import com.app.appellas.viewmodel.user.AuthViewModel
import com.app.appellas.viewmodel.user.ContactViewModel
import com.google.android.gms.location.*

class DialogDeleteContact: DialogFragment() {

    private var mBinding: DialogDeleteContactBinding? = null
    private val binding get() = mBinding!!

    private val args: DialogDeleteContactArgs by navArgs()

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val contactViewModel: ContactViewModel by viewModels {
        ViewModelFactory(userApp.contactRepository)
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
        mBinding = DialogDeleteContactBinding.inflate(inflater, container, false)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()
        setUpObservers()
    }

    private fun setNavigationListeners() {
        binding.dialogConfirmRegisterActionCancel.setOnClickListener {
            dismiss()
        }

        try {
            contactViewModel.userData.observe(viewLifecycleOwner) { user ->
                binding.dialogConfirmRegisterActionOk.setOnClickListener {
                    contactViewModel.deleteContact(user[0].accessToken, args.id)
                }
            }
        } catch (e: Exception){
            Log.d("Error", "Error")
        }
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        contactViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState)
        }
    }

    private fun handleUIState(uiState: UIState<Int>?) {
        when(uiState) {
            is UIState.Loading -> {
                showProgressBar()
            }
            is UIState.Success -> {
                endShowProgressBar()
                val direction = DialogDeleteContactDirections
                    .actionDialogDeleteContactToContactsFragment()
                NavHostFragment.findNavController(this).navigate(direction)
            }
            is UIState.Error -> {
                endShowProgressBar()
                Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                //activity?.applicationContext?.let { showToast.showToast(it, layout, toastText, uiState.message) }
            }
            else -> {

            }
        }
    }

    private fun endShowProgressBar() {
        binding.dialogDeleteProgressBar.visibility = View.GONE
        binding.dialogAlertContainer.alpha = 1F
    }

    private fun showProgressBar() {
        binding.dialogDeleteProgressBar.visibility = View.VISIBLE
        binding.dialogAlertContainer.alpha = 0.4F
    }

}
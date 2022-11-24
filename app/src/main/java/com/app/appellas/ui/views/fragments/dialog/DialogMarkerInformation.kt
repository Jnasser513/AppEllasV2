package com.app.appellas.ui.views.fragments.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.DialogMarkerInformationBinding
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminLocationViewModel

class DialogMarkerInformation: DialogFragment() {

    private var mBinding: DialogMarkerInformationBinding? = null
    private val binding get() = mBinding!!

    private val args: DialogMarkerInformationArgs by navArgs()

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val locationViewModel: AdminLocationViewModel by viewModels {
        ViewModelFactory(userApp.adminLocationRepository)
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
        mBinding = DialogMarkerInformationBinding.inflate(inflater, container, false)

        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        locationViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
            handleUIState(uiState)
        }
    }

    private fun handleUIState(uiState: UIState<Int>?) {
        when (uiState) {
            is UIState.Loading -> {
                showProgressBar()
            }
            is UIState.Success -> {
                endShowProgressBar()
                Log.d("Success", "SUCCESS")
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
        binding.dialogMarkerInformationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.dialogMarkerInformationProgressBar.visibility = View.VISIBLE
    }

    private fun initView() {
        locationViewModel.userData.observe(viewLifecycleOwner) { user ->
            locationViewModel.getLocationDetail(user[0].accessToken, args.id)
        }

        locationViewModel.locationDetail.observe(viewLifecycleOwner) { detail ->
            binding.dialogMarkerTitle.text = detail.titulo
            binding.dialogMarkerDescription.text = detail.descripcion
        }
    }

}
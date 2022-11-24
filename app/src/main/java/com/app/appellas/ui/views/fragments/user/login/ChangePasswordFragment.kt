package com.app.appellas.ui.views.fragments.user.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.ChangePasswordFragmentBinding
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.user.AuthViewModel

class ChangePasswordFragment: Fragment() {

    private var mBinding: ChangePasswordFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(userApp.loginRepository)
    }

    private val args: ChangePasswordFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginFragment)
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
        mBinding = ChangePasswordFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.changePasswordEdit.doAfterTextChanged { message ->
            authViewModel.passwordChange.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()
        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        authViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
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
                Log.d("SUccess", "SUCCESS")
                val direction = ChangePasswordFragmentDirections
                    .actionChangePasswordFragmentToLoginFragment()
                findNavController().navigate(direction)
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
        binding.changePasswordActionChange.isClickable = true
        binding.changePasswordProgressBar.visibility = View.GONE
        binding.changePasswordActionChange.setTextColor(resources.getColor(R.color.secondary_color))
    }

    private fun showProgressBar() {
        binding.changePasswordActionChange.isClickable = false
        binding.changePasswordProgressBar.visibility = View.VISIBLE
        binding.changePasswordActionChange.setTextColor(resources.getColor(android.R.color.transparent))
    }

    private fun setUpListeners() {
        binding.changePasswordActionChange.setOnClickListener {
            if(binding.changePasswordEdit.text.toString() != binding.changePasswordRepeatEdit.text.toString()) {
                Toast.makeText(requireContext(), "La contraseña debe coincidir", Toast.LENGTH_SHORT)
                    .show()
            } else {
                authViewModel.changePassword(args.code)
            }
        }
    }

    private fun setNavigationListeners() {
        val navController = findNavController()

        binding.changePasswordActionReturn.setOnClickListener {
            val direction = ChangePasswordFragmentDirections
                .actionChangePasswordFragmentToLoginFragment()
            navController.navigate(direction)
        }

        binding.changePasswordActionReturn2.setOnClickListener {
            val direction = ChangePasswordFragmentDirections
                .actionChangePasswordFragmentToLoginFragment()
            navController.navigate(direction)
        }
    }

}
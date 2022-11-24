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
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.EnterEmailFragmentBinding
import com.app.appellas.ui.views.fragments.user.CreateBlogFragmentDirections
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminAdvisorViewModel
import com.app.appellas.viewmodel.user.AuthViewModel

class EnterEmailFragment: Fragment() {

    private var mBinding: EnterEmailFragmentBinding? = null
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
        mBinding = EnterEmailFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.enterEmailEdit.doAfterTextChanged { message ->
            authViewModel.emailRecovery.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                val direction = EnterEmailFragmentDirections
                    .actionEnterEmailFragmentToVerifyRecoveryCodeFragment(binding.enterEmailEdit.text.toString())
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
        binding.enterEmailActionSend.isClickable = true
        binding.enterEmailProgressBar.visibility = View.GONE
        binding.enterEmailActionSend.setTextColor(resources.getColor(R.color.secondary_color))
    }

    private fun showProgressBar() {
        binding.enterEmailActionSend.isClickable = false
        binding.enterEmailProgressBar.visibility = View.VISIBLE
        binding.enterEmailActionSend.setTextColor(resources.getColor(android.R.color.transparent))
    }

    private fun setUpListeners() {
        binding.enterEmailActionSend.setOnClickListener {
            authViewModel.sendRecoveryCode()
        }

        binding.changePasswordActionReturn2.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}
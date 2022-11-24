package com.app.appellas.ui.views.fragments.user.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.CreateAccountFragmentBinding
import com.app.appellas.viewmodel.user.AuthViewModel

class CreateAccountFragment : Fragment() {

    private var mBinding: CreateAccountFragmentBinding? = null
    private val binding get() = mBinding!!

    private val authViewModel: AuthViewModel by activityViewModels()

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
        mBinding = CreateAccountFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = authViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.createAccountNameEdit.doAfterTextChanged { message ->
            authViewModel.nameRegister.value = message.toString()
        }

        binding.createAccountEmailEdit.doAfterTextChanged { message ->
            authViewModel.emailRegister.value = message.toString()
        }

        binding.createAccountPhoneEdit.doAfterTextChanged { message ->
            authViewModel.phoneRegister.value = message.toString()
        }

        binding.createAccountPasswordEdit.doAfterTextChanged { message ->
            authViewModel.passwordRegister.value = message.toString()
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
                val direction = CreateAccountFragmentDirections
                    .actionCreateAccountFragmentToDialogConfirmRegister()
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
        binding.createAccountProgressBar.visibility = View.GONE
        binding.createAccountActionRegister.setTextColor(resources.getColor(R.color.secondary_color))
    }

    private fun showProgressBar() {
        binding.createAccountProgressBar.visibility = View.VISIBLE
        binding.createAccountActionRegister.setTextColor(resources.getColor(android.R.color.transparent))
    }

    private fun setUpListeners() {
        onCreateAccountListener()
    }

    private fun onCreateAccountListener() {
        binding.createAccountActionRegister.setOnClickListener {
            if (binding.createAccountNameEdit.text.toString().isEmpty() ||
                binding.createAccountEmailEdit.text.toString().isEmpty() ||
                binding.createAccountPhoneEdit.text.toString().isEmpty() ||
                binding.createAccountRepeatEdit.text.toString().isEmpty() ||
                binding.createAccountPasswordEdit.text.toString().isEmpty()
            ) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if(binding.createAccountPasswordEdit.text.toString() == binding.createAccountRepeatEdit.text.toString()) {
                    authViewModel.register()
                } else {
                    Toast.makeText(requireContext(), "La contraseña debe coincidir", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setNavigationListeners() {
        val navController = findNavController()

        binding.loginActionRegisterAccount.setOnClickListener {
            val direction = CreateAccountFragmentDirections
                .actionCreateAccountFragmentToLoginFragment()
            navController.navigate(direction)
        }

        binding.createAccountActionReturn.setOnClickListener {
            val direction = CreateAccountFragmentDirections
                .actionCreateAccountFragmentToLoginFragment()
            navController.navigate(direction)
        }
    }

}
package com.app.appellas.ui.views.fragments.admin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.CreateAdvisorFragmentBinding
import com.app.appellas.databinding.CreateContactFragmentBinding
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminAdvisorViewModel

class CreateAdvisorFragment : Fragment() {

    private var mBinding: CreateAdvisorFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val advisorViewModel: AdminAdvisorViewModel by viewModels {
        ViewModelFactory(userApp.adminAdvisorRepository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.adminAdvisoryFragment)
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
        mBinding = CreateAdvisorFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = advisorViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.createContactNameEdit.doAfterTextChanged { message ->
            advisorViewModel.name.value = message.toString()
        }

        binding.createContactNumberEdit.doAfterTextChanged { message ->
            advisorViewModel.number.value = message.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()
        setUpListeners(view)
        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        advisorViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
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
                val direction = CreateAdvisorFragmentDirections
                    .actionCreateContactFragmentToAdminAdvisoryFragment()
                findNavController().navigate(direction)
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
        binding.createContactProgressBar.visibility = View.GONE
        binding.createContactActionCreate.setTextColor(resources.getColor(R.color.secondary_color))
    }

    private fun showProgressBar() {
        binding.createContactProgressBar.visibility = View.VISIBLE
        binding.createContactActionCreate.setTextColor(resources.getColor(android.R.color.transparent))
    }

    private fun setUpListeners(view: View) {
        binding.createContactActionCreate.setOnClickListener {
            val type = binding.createAccountRadioGroup.checkedRadioButtonId
            val selected = view.findViewById<RadioButton>(type)
            if (selected != null) {
                advisorViewModel.userData.observe(viewLifecycleOwner) { user ->
                    when (selected.text.toString()) {
                        "Medico" -> {
                            advisorViewModel.createAdvisor(user[0].accessToken, 1)
                        }
                        "Legal" -> {
                            advisorViewModel.createAdvisor(user[0].accessToken, 2)
                        }
                        "Psicologo" -> {
                            advisorViewModel.createAdvisor(user[0].accessToken, 3)
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Debe seleccionar un tipo de asesor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setNavigationListeners() {
        val navController = findNavController()

        binding.createBlogActionReturn.setOnClickListener {
            val direction = CreateAdvisorFragmentDirections
                .actionCreateContactFragmentToAdminAdvisoryFragment()
            navController.navigate(direction)
        }
    }

}
package com.app.appellas.ui.views.fragments.admin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.models.entities.User
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.AdminManageAccFragmentBinding
import com.app.appellas.ui.adapters.AccountsAdapter
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminUserViewModel

class AdminManageAccountFragment : Fragment() {

    private var mBinding: AdminManageAccFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val userViewModel: AdminUserViewModel by viewModels {
        ViewModelFactory(userApp.adminUserRepository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.adminHomeFragment)
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
        mBinding = AdminManageAccFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = userViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()

        try {
            userViewModel.userData.observe(viewLifecycleOwner) { user ->
                setUpRecyclerView(user)

                userViewModel.stateUI2.observe(viewLifecycleOwner) {
                    it?.let {
                        if(it) {
                            setUpRecyclerView(user)
                        }
                        userViewModel.endChangeState()
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        userViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
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
        binding.adminManageAccProgressBar.visibility = View.GONE
        binding.adminManageAccRecyclerview.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        binding.adminManageAccProgressBar.visibility = View.VISIBLE
        binding.adminManageAccRecyclerview.visibility = View.VISIBLE
    }

    private fun setNavigationListeners() {
        val navController = findNavController()

        binding.adminManageAccountReturn.setOnClickListener {
            val direction = AdminManageAccountFragmentDirections
                .actionAdminManageAccountFragmentToAdminHomeFragment()
            navController.navigate(direction)
        }
    }

    private fun setUpRecyclerView(user: List<User>) {
        try {
            userViewModel.getAllUsers(user[0].accessToken)
            binding.adminManageAccRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = AccountsAdapter {
                    userViewModel.deleteUser(user[0].accessToken, it)
                }
            }

            userViewModel.usersList.observe(viewLifecycleOwner) { list ->
                (binding.adminManageAccRecyclerview.adapter as AccountsAdapter).setData(list)
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

}
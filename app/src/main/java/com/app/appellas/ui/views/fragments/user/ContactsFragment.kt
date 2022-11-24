package com.app.appellas.ui.views.fragments.user

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
import com.app.appellas.databinding.ContactsFragmentBinding
import com.app.appellas.ui.adapters.AccountsAdapter
import com.app.appellas.ui.adapters.ContactAdapter
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.user.ContactViewModel

class ContactsFragment: Fragment() {

    private var mBinding: ContactsFragmentBinding? = null
    private val binding get() = mBinding!!

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
                    findNavController().navigate(R.id.homeFragment)
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
        mBinding = ContactsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        contactViewModel.userData.observe(viewLifecycleOwner) { user ->
            setUpRecyclerView(user)

            contactViewModel.stateUI2.observe(viewLifecycleOwner) {
                it?.let {
                    if(it) {
                        setUpRecyclerView(user)
                    }
                    contactViewModel.endChangeState()
                }
            }
        }
        setUpObservers()
    }

    private fun setUpRecyclerView(user: List<User>) {
        try {
            contactViewModel.getAllContacts(user[0].accessToken, user[0].id)
            binding.contactRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ContactAdapter()
            }

            contactViewModel.contactList.observe(viewLifecycleOwner) { list ->
                (binding.contactRecyclerview.adapter as ContactAdapter).setData(list)
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

    private fun setUpListeners() {
        binding.createBlogActionReturn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.blogsActionAdd.setOnClickListener {
            val direction = ContactsFragmentDirections
                .actionContactsFragmentToCreateContactFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
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
                Log.d("SUccess", "SUCCESS")
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
        binding.contactProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.contactProgressBar.visibility = View.VISIBLE
    }

}
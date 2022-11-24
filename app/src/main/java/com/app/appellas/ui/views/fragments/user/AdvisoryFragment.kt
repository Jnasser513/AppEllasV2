package com.app.appellas.ui.views.fragments.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.models.entities.User
import com.app.appellas.databinding.AdvisoryFragmentBinding
import com.app.appellas.ui.adapters.LegalAdvisorAdapter
import com.app.appellas.ui.adapters.MedicalAdvisorAdapter
import com.app.appellas.ui.adapters.PsychologistAdvisoryAdapter
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminAdvisorViewModel

class AdvisoryFragment: Fragment() {

    private var mBinding: AdvisoryFragmentBinding? = null
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
        mBinding = AdvisoryFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        advisorViewModel.userData.observe(viewLifecycleOwner) { user ->
            setUpMedicalRecyclerView(user)
            setUpLegalRecyclerView(user)
            setUpPsychologyRecyclerView(user)
        }
    }

    private fun setUpMedicalRecyclerView(user: List<User>) {
        try {
            advisorViewModel.getMedicalAdvisor(user[0].accessToken)
            binding.advisoryMedicalRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = MedicalAdvisorAdapter()
            }

            advisorViewModel.medicalList.observe(viewLifecycleOwner) { list ->
                (binding.advisoryMedicalRecyclerview.adapter as MedicalAdvisorAdapter).setData(list)
                (binding.advisoryMedicalRecyclerview.adapter as MedicalAdvisorAdapter).setRol(user[0].rol)
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

    private fun setUpLegalRecyclerView(user: List<User>) {
        try {
            advisorViewModel.getLegalAdvisor(user[0].accessToken)
            binding.advisoryLegalRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = LegalAdvisorAdapter()
            }

            advisorViewModel.legalList.observe(viewLifecycleOwner) { list ->
                (binding.advisoryLegalRecyclerview.adapter as LegalAdvisorAdapter).setData(list)
                (binding.advisoryLegalRecyclerview.adapter as LegalAdvisorAdapter).setRol(user[0].rol)
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

    private fun setUpPsychologyRecyclerView(user: List<User>) {
        try {
            advisorViewModel.getPsycologicalAdvisor(user[0].accessToken)
            binding.advisoryPsycologicalRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = PsychologistAdvisoryAdapter()
            }

            advisorViewModel.psicologyList.observe(viewLifecycleOwner) { list ->
                (binding.advisoryPsycologicalRecyclerview.adapter as PsychologistAdvisoryAdapter).setData(list)
                (binding.advisoryPsycologicalRecyclerview.adapter as PsychologistAdvisoryAdapter).setRol(user[0].rol)
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}
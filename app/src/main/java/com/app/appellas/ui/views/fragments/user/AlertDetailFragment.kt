package com.app.appellas.ui.views.fragments.user

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.appellas.R
import com.app.appellas.databinding.AlertDetailFragmentBinding
import com.app.appellas.viewmodel.user.HomeViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class AlertDetailFragment: Fragment() {

    private var mBinding: AlertDetailFragmentBinding? = null
    private val binding get() = mBinding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var sharedPreferences: SharedPreferences
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = AlertDetailFragmentBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("appellas", Context.MODE_PRIVATE)!!
        token = sharedPreferences.getString("token_session", "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.goAlertDetail.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    viewModel.notificationId.observe(viewLifecycleOwner) { id ->
                        viewModel.getAlertDetail(token!!, id.toInt())
                    }
                }
                viewModel.endGoAlertDetail()
            }
        }

        binding.btnReturn.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        viewModel.alertDetail.observe(viewLifecycleOwner) { detail ->
            Log.d("DETAIL", detail.toString())
            binding.createBlogTitleEdit.text = detail.titulo
            binding.createBlogSubtitleEdit.text = detail.descripcion
            Glide.with(requireContext())
                .load(detail.log_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.createBlogImage)
        }
    }

}
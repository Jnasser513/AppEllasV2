package com.app.appellas.ui.views.fragments.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.CreateBlogFragmentBinding
import com.app.appellas.ui.utils.ConvertBase64
import com.app.appellas.viewmodel.user.BlogViewModel
import com.app.appellas.viewmodel.ViewModelFactory

class CreateBlogFragment: Fragment() {

    private var mBinding: CreateBlogFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val blogViewModel: BlogViewModel by viewModels {
        ViewModelFactory(userApp.blogRepository)
    }

    private val converter = ConvertBase64()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.blogsFragment)
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
        mBinding = CreateBlogFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = blogViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.createBlogTitleEdit.doAfterTextChanged { message ->
            blogViewModel.title.value = message.toString()
        }

        binding.createBlogSubtitleEdit.doAfterTextChanged { message ->
            blogViewModel.subtitle.value = message.toString()
        }

        binding.createBlogDescriptionEdit.doAfterTextChanged { message ->
            blogViewModel.description.value = message.toString()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpNavigationListeners()
        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        blogViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
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
                val direction = CreateBlogFragmentDirections
                    .actionCreateBlogFragmentToDialogCreateBlog()
                findNavController().navigate(direction)
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
        binding.createBlogProgressBar.visibility = View.GONE
        binding.createBlogActionShare.setTextColor(resources.getColor(R.color.secondary_color))
    }

    private fun showProgressBar() {
        binding.createBlogProgressBar.visibility = View.VISIBLE
        binding.createBlogActionShare.setTextColor(resources.getColor(android.R.color.transparent))
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setUpListeners() {
        onCreateListener()
        onPhotoClickListener()
    }

    private fun onCreateListener() {
        binding.createBlogActionShare.setOnClickListener {
            blogViewModel.userData.observe(viewLifecycleOwner) { user ->
                blogViewModel.voucher.observe(viewLifecycleOwner) { voucher ->
                    if(binding.createBlogTitleEdit.text.isNullOrEmpty() || binding.createBlogTitleEdit.text.isNullOrEmpty() ||
                        binding.createBlogDescriptionEdit.text.isNullOrEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Debe llenar todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if(voucher.toString() == "") {
                            Toast.makeText(
                                requireContext(),
                                "Debe subir una foto",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            blogViewModel.createBlog(user[0].accessToken, user[0].id)
                        }
                    }
                }
            }
        }
    }

    private fun setUpNavigationListeners() {
        binding.createBlogActionReturn.setOnClickListener {
            val navController = findNavController()
            val direction = CreateBlogFragmentDirections
                .actionCreateBlogFragmentToBlogsFragment()
            navController.navigate(direction)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun onPhotoClickListener() {
        binding.createBlogImage.setOnClickListener {
            verifyStoragePermissions()
        }
    }

    private val requestLocationLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if(uri != null) {
                val encode = converter.uriToBase64(uri, requireActivity().contentResolver)
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, Uri.parse(
                    uri.toString()
                ))
                binding.createBlogImage.setImageBitmap(bitmap)
                blogViewModel.getVoucherImage(encode)
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun verifyStoragePermissions() {
        if (Environment.isExternalStorageManager()) {
            requestLocationLauncher.launch("image/*")
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )

            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

}
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
import com.app.appellas.databinding.BlogsFragmentBinding
import com.app.appellas.ui.adapters.BlogsAdapter
import com.app.appellas.viewmodel.user.BlogViewModel
import com.app.appellas.viewmodel.ViewModelFactory

class BlogsFragment: Fragment() {

    private var mBinding: BlogsFragmentBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val blogViewModel: BlogViewModel by viewModels {
        ViewModelFactory(userApp.blogRepository)
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
        mBinding = BlogsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpNavListeners()

        blogViewModel.userData.observe(viewLifecycleOwner) { user ->
            setUpRecyclerView(user)
        }
    }

    private fun setUpRecyclerView(user: List<User>) {
        try {
            binding.blogsRecyclerview.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext())

                //Estamos observando el objeto nulo
                adapter = BlogsAdapter()
            }

            blogViewModel.getAllBlogs(user[0].accessToken)
            //user?.token?.let { appViewModel.getGeneratedPasses(it) }

            blogViewModel.blogsList.observe(viewLifecycleOwner) { list ->
                if (list != null) {
                    (binding.blogsRecyclerview.adapter as BlogsAdapter).setData(
                        list
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

    private fun setUpNavListeners() {
        binding.blogsActionAdd.setOnClickListener {
            val navController = findNavController()
            val direction = BlogsFragmentDirections
                .actionBlogsFragmentToCreateBlogFragment()
            navController.navigate(direction)
        }
    }

}
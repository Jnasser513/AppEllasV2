package com.app.appellas.ui.views.fragments.admin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.appellas.AppApplication
import com.app.appellas.R
import com.app.appellas.data.models.dtos.body.UpdateLocationBody
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.AdminInformationBinding
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.admin.AdminLocationViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class AdminInformationMapFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private var mBinding: AdminInformationBinding? = null
    private val binding get() = mBinding!!

    private lateinit var map: GoogleMap
    private var array: ArrayList<Marker> = arrayListOf()
    private var locationList: ArrayList<UpdateLocationBody> = arrayListOf()

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val locationViewModel: AdminLocationViewModel by viewModels {
        ViewModelFactory(userApp.adminLocationRepository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
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
        mBinding = AdminInformationBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = locationViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        createMapFragment()

        return binding.root
    }

    private fun createMapFragment() {
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.admin_information_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()
        locationViewModel.userData.observe(viewLifecycleOwner) { user ->
            try {
                locationViewModel.getAllLocations(user[0].accessToken)
            } catch (e: Exception) {
                Log.d("Error", "Error")
            }
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        observeState()
    }

    private fun observeState() {
        locationViewModel.stateUI.observe(viewLifecycleOwner) { uiState ->
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
        binding.adminInformationMapProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.adminInformationMapProgressBar.visibility = View.VISIBLE
    }

    private fun setNavigationListeners() {
        val navController = findNavController()
        binding.adminManageAccountReturn.setOnClickListener {
            val direction = AdminInformationMapFragmentDirections
                .actionAdminInformationMapFragmentToAdminHomeFragment()
            navController.navigate(direction)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarkers()
        googleMap.setOnMarkerClickListener(this)
    }

    private fun createMarkers() {
        locationViewModel.locationList.observe(viewLifecycleOwner) { locations ->
            for (i in locations.indices) {
                val coordinates = LatLng(locations[i].latitud, locations[i].longitud)
                val markerOption =
                    MarkerOptions().position(coordinates).title(locations[i].user.nombre)
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.warning))
                val marker = map.addMarker(markerOption)
                val updateBody = UpdateLocationBody(
                    locations[i].id,
                    marker!!.id
                )
                locationList.add(updateBody)
                array.add(marker)
            }
            locationViewModel.userData.observe(viewLifecycleOwner) { user ->
                try {
                    locationViewModel.updateLocations(user[0].accessToken, locationList)
                } catch (e: Exception) {
                    Log.d("Error", "Error")
                }
            }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        for (i in 0 until array.size) {
            if (p0 == array[i]) {
                val direction = AdminInformationMapFragmentDirections
                    .actionAdminInformationMapFragmentToDialogMarkerInformation(p0.id)
                findNavController().navigate(direction)
            }
        }
        return false
    }
}
package com.app.appellas.ui.views.fragments.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
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

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    var lat: String = ""
    var lon: String = ""

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        createMapFragment()

        getLastLocation()

        return binding.root
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        //findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()

                        val sydney = LatLng(location.latitude, location.longitude)
                        lat = "${location.latitude}"
                        lon = "${location.longitude}"
                        map.isMyLocationEnabled = true;
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14.5f))
                        map.animateCamera(CameraUpdateFactory.zoomIn());
                        map.animateCamera(CameraUpdateFactory.zoomTo(14.5f), 2000, null);
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Activa la ubiación", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        @SuppressLint("MissingPermission")
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location? = locationResult.lastLocation
            //findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            //findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            val sydney = mLastLocation?.let { LatLng(it.latitude, it.longitude) }

            sydney?.let { CameraUpdateFactory.newLatLngZoom(it,14.5f) }?.let { map.moveCamera(it) }
            map.animateCamera(CameraUpdateFactory.zoomIn());
            map.animateCamera(CameraUpdateFactory.zoomTo(14.5f), 2000, null);
        }
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
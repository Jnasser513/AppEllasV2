package com.app.appellas.ui.views.fragments.dialog

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.app.appellas.AppApplication
import com.app.appellas.data.models.dtos.body.CreateLocationBody
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import com.app.appellas.databinding.DialogAlertBinding
import com.app.appellas.interactors.PostAlertaWigdet
import com.app.appellas.viewmodel.ViewModelFactory
import com.app.appellas.viewmodel.user.LocationViewModel
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogAlertFragment : DialogFragment() {

    private var mBinding: DialogAlertBinding? = null
    private val binding get() = mBinding!!

    private val userApp by lazy {
        activity?.application as AppApplication
    }

    private val locationViewModel: LocationViewModel by viewModels {
        ViewModelFactory(userApp.locationRepository)
    }

    @Inject
    lateinit var postAlertWidget: PostAlertaWigdet

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val PERMISSION_ID = 42

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogAlertBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = locationViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        postAlertWidget = PostAlertaWigdet(AppAPI.widgetService)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationListeners()
        setUpObservers()
    }

    private fun allPermissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(
            activity?.baseContext!!,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getActualLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                        var location: Location = task.result
                        if (location == null) {
                            requestNewLocation()
                        } else {
                            locationViewModel.userData.observe(viewLifecycleOwner) { user ->
                                locationViewModel.sendLocation(
                                    user[0].accessToken,
                                    user[0].id,
                                    task.result.latitude,
                                    task.result.longitude
                                )
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Activar permisos de ubicacion",
                    Toast.LENGTH_SHORT
                )
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }
    }

    private fun requestNewLocation() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var mLastLocation: Location = p0.lastLocation!!
            locationViewModel.userData.observe(viewLifecycleOwner) { user ->
                locationViewModel.sendLocation(
                    user[0].accessToken,
                    user[0].id,
                    mLastLocation.latitude,
                    mLastLocation.longitude
                )
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
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
                locationViewModel.location.observe(viewLifecycleOwner) { location ->
                    val direction = DialogAlertFragmentDirections
                        .actionDialogAlertFragmentToDialogDescriptionAlert(location.id.toLong())
                    NavHostFragment.findNavController(this).navigate(direction)
                }
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
        binding.dialogAlertProgressBar.visibility = View.GONE
        binding.dialogAlertContainer.alpha = 1F
    }

    private fun showProgressBar() {
        binding.dialogAlertProgressBar.visibility = View.VISIBLE
        binding.dialogAlertContainer.alpha = 0.4F
    }

    private fun setNavigationListeners() {
        binding.dialogConfirmRegisterActionCancel.setOnClickListener {
            dismiss()
        }

        binding.dialogConfirmRegisterActionOk.setOnClickListener {
            if (allPermissionsGrantedGPS()) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                getActualLocation()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_ID
                )
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS_GPS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

}
package com.app.appellas.ui.views.fragments.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.appellas.R
import com.app.appellas.databinding.HomepageFragmentBinding
import com.app.appellas.location.LocationService
import com.app.appellas.ui.views.fragments.dialog.DialogAlertFragment
import java.util.jar.Manifest

class HomeFragment: Fragment() {

    private var mBinding: HomepageFragmentBinding? = null
    private val binding get() = mBinding!!

    lateinit var sharedPref: SharedPreferences
    var token_session: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var id_user: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    activity?.finish()
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
        mBinding = HomepageFragmentBinding.inflate(inflater, container, false)

        sharedPref = requireContext().getSharedPreferences("appellas", Context.MODE_PRIVATE)
        token_session = sharedPref.getString("token_session", "")
        latitude = sharedPref.getString("latitude", "")
        longitude = sharedPref.getString("longitude", "")
        id_user = sharedPref.getString("id_user", "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Shared", "$token_session $latitude $longitude $id_user")

        if(checkPermissions()) {
            if(isLocationEnabled()) {
                Intent(requireActivity().applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    requireActivity().startService(this)
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
            requestLocationPermission()
        }

        setUpNavigationListeners()
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
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            0 -> when{
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    Intent(requireActivity().applicationContext, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        requireActivity().startService(this)
                    }
                    Log.d("ENTRO", "LOCATION")
                }
            }
        }
    }

    private fun setUpNavigationListeners() {
        navToBlogFragment()
        navToAdvisoryFragment()
        navToInformationFragment()
    }

    private fun navToBlogFragment() {
        binding.homepageBlogContainer.setOnClickListener {
            val navController = findNavController()
            val direction = HomeFragmentDirections
                .actionHomeFragmentToBlogsFragment()
            navController.navigate(direction)
        }
    }

    private fun navToAdvisoryFragment() {
        binding.homepageAdvisoryContainer.setOnClickListener {
            val navController = findNavController()
            val direction = HomeFragmentDirections
                .actionHomeFragmentToAdvisoryFragment()
            navController.navigate(direction)
        }
    }

    private fun navToInformationFragment() {
        binding.homepageInformationContainer.setOnClickListener {
            val navController = findNavController()
            val direction = HomeFragmentDirections
                .actionHomeFragmentToInformationFragment()
            navController.navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}
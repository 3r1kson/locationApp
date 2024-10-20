package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private lateinit var volumeKeyListener: VolumeKeyListener
    private val viewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volumeKeyListener = VolumeKeyListener(
            context = this,
            viewModel = viewModel,
            locationUtils = LocationUtils(this)
        )

        enableEdgeToEdge()
        setContent {
            LocationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(volumeKeyListener)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeKeyListener.onVolumeDownPress()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

@Composable
fun MyApp(volumeKeyListener: VolumeKeyListener) {
    val context = LocalContext.current
    val viewModel: LocationViewModel = viewModel()
    val locationUtils = LocationUtils(context)

    LocationDisplay(
        locationUtils = locationUtils,
        viewModel = viewModel,
        volumeKeyListener = volumeKeyListener,
        context = context
    )
}

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    volumeKeyListener: VolumeKeyListener,
    context: Context
) {
    val location = viewModel.location.value
    val address = location?.let {
        locationUtils.reverseGeocodelocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {

                locationUtils.requestLocationUpdates(viewModel)

            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)

                if (rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Location permission is required for this feature to work",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Location permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (location != null) {
            Text("Address: ${location.latitude} ${location.longitude} \n" +
                    "$address")
        } else {
            Text("Location not available")
        }

        Button(onClick = {
            println(location)
            if (locationUtils.hasLocationPermission(context)) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }

            location?.let {
                showLocationNotification(context, it, address ?: "Address not available")
            }
        }) {
            Text("Get Location")
        }
    }
}
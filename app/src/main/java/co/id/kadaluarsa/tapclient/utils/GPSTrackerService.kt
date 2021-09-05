package co.id.kadaluarsa.tapclient.utils

import android.Manifest
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import co.id.kadaluarsa.tapclient.utils.GPSTrackerService
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.lang.Exception

class GPSTrackerService(private var mContext: Context) : LocationListener {

    // Flag for GPS status
    private var isGPSEnabled = false

    // Flag for network status
    private var isNetworkEnabled = false

    // Flag for GPS status
    private var canGetLocation = false
    private var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    // Declaring a Location Manager
    private lateinit var locationManager: LocationManager

    init {
        getLocation()
    }

    private fun getLocation(): Location? {
        try {
            locationManager = mContext
                .getSystemService(LOCATION_SERVICE) as LocationManager

            // Getting GPS status
            isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // Getting network status
            isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return null
                    }
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location != null) {
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    fun stopUsingGPS() {
        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(this@GPSTrackerService)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double = location?.latitude ?: latitude

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double = location?.longitude ?: longitude


    /**
     * Function to check GPS/Wi-Fi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean = canGetLocation

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    fun showSettingsAlert(action: () -> Unit) {
        val alertDialog = AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is Required For Application")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            action.invoke()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onLocationChanged(_location: Location) {
        location = _location
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
                ).toLong()
    }
}
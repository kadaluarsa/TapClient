package co.id.kadaluarsa.tapclient

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import co.id.kadaluarsa.tapclient.utils.GPSTrackerService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    private val locationPermission: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val gpsRequest: Int = 11
    private val requestLocationRequest: Int = 22
    private val forcePermissonRequest: Int = 33

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val titles = arrayOf("Touch", "History")
        viewPager.adapter = FragmentAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(
            tabs, viewPager
        ) { tab, position -> tab.text = titles[position] }.attach()
        checkIfUserAllowedLocation()
        checkIfUserEnableGPS()
    }

    private fun checkIfUserEnableGPS() {
        val gps = GPSTrackerService(this@MainActivity)
        if (gps.canGetLocation().not()) {
            gps.showSettingsAlert {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, gpsRequest)
            }
        }
    }

    private fun checkIfUserAllowedLocation() {
        checkPermission {
            // Requesting the permission
            Toast.makeText(this@MainActivity, "should allow permission", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this@MainActivity,
                locationPermission,
                requestLocationRequest
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestLocationRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                //force user to enable location access
                showForcePermisson()
            }
        }
    }

    private fun checkPermission(openSetting: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            openSetting.invoke()
            return
        }
    }


    private fun showForcePermisson() {
        val alertDialog = AlertDialog.Builder(this@MainActivity)
        // Setting Dialog Title
        alertDialog.setTitle("Location Permission is Require For Application")
        // Setting Dialog Message
        alertDialog.setMessage("Do you want to go to settings menu?")
        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            startInstalledAppDetailsActivity(this@MainActivity)
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun startInstalledAppDetailsActivity(context: Activity) {
        val i = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivityForResult(i, forcePermissonRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == forcePermissonRequest) {
            Handler().postDelayed({
                checkIfUserAllowedLocation()
            }, 500)
        } else if (requestCode == gpsRequest) {
            Handler().postDelayed({
                checkIfUserEnableGPS()
            }, 500)
        }
    }
}
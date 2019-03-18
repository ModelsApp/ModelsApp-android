package com.square.android.ui.activity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.utils.PermissionsManager
import java.lang.Exception

private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1_000L
private const val DEFAULT_MAX_WAIT_TIME = 30_000L

abstract class LocationActivity : BaseActivity(),
        LocationEngineCallback<LocationEngineResult>, PermissionsListener {
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null

    abstract fun locationGotten(lastLocation: Location?)

    protected open fun locationAllowed() {}

    override fun onSuccess(result: LocationEngineResult) {
        locationGotten(result.lastLocation)
    }

    override fun onFailure(exception: Exception) {
        showMessage(R.string.location_failed_message)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>) {
        showMessage(R.string.permsission_needed)
    }

    private fun tryInitLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initLocation()

            locationAllowed()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)
        }
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            initLocation()

            locationAllowed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tryInitLocation()
    }

    override fun onStart() {
        super.onStart()

        locationEngine?.let { requestLocationUpdates() }
    }

    override fun onStop() {
        super.onStop()

        locationEngine?.removeLocationUpdates(this)
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        locationEngine?.getLastLocation(this)

        requestLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val request = LocationEngineRequest
                .Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        locationEngine?.requestLocationUpdates(request, this, null)
    }
}
package com.square.android.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.utils.PermissionsManager
import java.lang.Exception

private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1_000L
private const val DEFAULT_MAX_WAIT_TIME = 30_000L

abstract class LocationFragment : BaseFragment(), LocationEngineCallback<LocationEngineResult>, PermissionsListener {
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null

    open fun isLocationEnabled() = true

    abstract fun locationGotten(lastLocation: Location?)

    protected open fun locationAllowed() {}

    override fun onSuccess(result: LocationEngineResult) {
        locationGotten(result.lastLocation)
    }

    override fun onFailure(exception: Exception) {
        showMessage(R.string.location_failed_message)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>) {
        showMessage(R.string.permission_needed)
    }

    private fun tryInitLocation() {

        if(PermissionsManager.areLocationPermissionsGranted(activity)){
            if(repository.getGeolocationAllowed()){
                initLocation()

                locationAllowed()
            } else{
                if(!repository.getLocationDontAsk()){
                    showLocationDialog()
                }
            }

        } else{
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)
        }
    }

    private fun showLocationDialog() {
        val dialog = MaterialDialog.Builder(activity!!)
                .cancelable(true)
                .content(R.string.permission_dialog_text)
                .contentColor(ContextCompat.getColor(activity!!, android.R.color.black))
                .title(R.string.location_permissions)
                .positiveText(R.string.yes)
                .positiveColor(ContextCompat.getColor(activity!!, R.color.nice_pink))
                .negativeText(R.string.no)
                .negativeColor(ContextCompat.getColor(activity!!, R.color.secondary_text))
                .checkBoxPrompt(getString(R.string.dont_ask_again),false) { buttonView, isChecked -> repository.setLocationDontAsk(isChecked) }
                .onPositive { dialog, which -> run{
                    repository.setGeolocationAllowed(true)
                    initLocation()
                    locationAllowed()
                } }
                .onNegative { dialog, which -> dialog.dismiss() }
                .build()

        dialog.show()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLocationEnabled()) tryInitLocation()
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
        locationEngine = LocationEngineProvider.getBestLocationEngine(activity!!)
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

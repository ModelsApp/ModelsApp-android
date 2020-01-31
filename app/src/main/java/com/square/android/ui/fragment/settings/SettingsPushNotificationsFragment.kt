package com.square.android.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.local.LocalDataManager
import com.square.android.presentation.presenter.settings.SettingsPushNotificationsPresenter
import com.square.android.presentation.view.settings.SettingsPushNotificationsView
import com.square.android.ui.fragment.BaseTabFragment
import kotlinx.android.synthetic.main.fragment_settings_push_notifications.*
import org.koin.android.ext.android.inject

class SettingsPushNotificationsFragment: BaseTabFragment(), SettingsPushNotificationsView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(): SettingsPushNotificationsFragment {

            return SettingsPushNotificationsFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: SettingsPushNotificationsPresenter

    @ProvidePresenter
    fun providePresenter() = SettingsPushNotificationsPresenter()

    private val localDataManager: LocalDataManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_push_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchAvailableSpots.isChecked = localDataManager.getSpotsCloseAllowed()
        switchNewLocations.isChecked = localDataManager.getNewLocationsAllowed()
        switchJobMatching.isChecked = localDataManager.getNewJobMatchingAllowed()
        switchEventsInCity.isChecked = localDataManager.getEventsInCityAllowed()
        switchCreditsAdded.isChecked = localDataManager.getCreditsAddedAllowed()
        switchFriendMessages.isChecked = localDataManager.getFriendMessagesAllowed()
        switchBusinessMessages.isChecked = localDataManager.getBusinessMessagesAllowed()

        availableSpotsLl.setOnClickListener {
            val checked = switchAvailableSpots.isChecked.not()
            switchAvailableSpots.isChecked = checked
            localDataManager.setSpotsCloseAllowed(checked)
        }

        newLocationsLl.setOnClickListener {
            val checked = switchNewLocations.isChecked.not()
            switchNewLocations.isChecked = checked
            localDataManager.setNewLocationsAllowed(checked)
        }

        jobMatchingLl.setOnClickListener {
            val checked = switchJobMatching.isChecked.not()
            switchJobMatching.isChecked = checked
            localDataManager.setNewJobMatchingAllowed(checked)
        }

        eventsInCityLl.setOnClickListener {
            val checked = switchEventsInCity.isChecked.not()
            switchEventsInCity.isChecked = checked
            localDataManager.setEventsInCityAllowed(checked)
        }

        creditsAddedLl.setOnClickListener {
            val checked = switchCreditsAdded.isChecked.not()
            switchCreditsAdded.isChecked = checked
            localDataManager.setCreditsAddedAllowed(checked)
        }

        friendMessagesLl.setOnClickListener {
            val checked = switchFriendMessages.isChecked.not()
            switchFriendMessages.isChecked = checked
            localDataManager.setFriendMessagesAllowed(checked)
        }

        businessMessagesLl.setOnClickListener {
            val checked = switchBusinessMessages.isChecked.not()
            switchBusinessMessages.isChecked = checked
            localDataManager.setBusinessMessagesAllowed(checked)
        }
    }

}
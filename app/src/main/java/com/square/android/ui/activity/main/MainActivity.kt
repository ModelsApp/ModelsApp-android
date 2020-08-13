package com.square.android.ui.activity.main

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.square.android.App
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.network.fcm.NotificationType
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.extensions.*
import com.square.android.presentation.presenter.explore.EventSelectedEvent
import com.square.android.presentation.presenter.explore.PlaceSelectedEvent
import com.square.android.presentation.presenter.main.MainPresenter
import com.square.android.presentation.presenter.place.PlaceExtras
import com.square.android.presentation.view.main.MainView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.campaignDetails.CampaignDetailsActivity
import com.square.android.ui.activity.event.*
import com.square.android.ui.fragment.profile.EditProfileFragment
import com.square.android.ui.activity.gallery.GalleryActivity
import com.square.android.ui.activity.gallery.USER_EXTRA
import com.square.android.ui.fragment.explore.campaignsList.CAMPAIGN_EXTRA_ID
import com.square.android.ui.activity.noConnection.NoConnectionActivity
import com.square.android.ui.activity.place.PLACE_EXTRA_DAY_SELECTED
import com.square.android.ui.activity.place.PLACE_EXTRA_ID
import com.square.android.ui.activity.place.PlaceActivity
import com.square.android.ui.activity.profile.*
import com.square.android.ui.activity.selectOffer.SelectOfferActivity
import com.square.android.ui.activity.settings.SettingsActivity
import com.square.android.ui.activity.start.StartActivity
import com.square.android.ui.activity.subscriptionError.SubscriptionErrorActivity
import com.square.android.ui.activity.tutorialVideos.TutorialVideosActivity
import com.square.android.ui.fragment.agenda.AgendaFragment
import com.square.android.ui.fragment.explore.ExploreFragment
import com.square.android.ui.fragment.explore.SearchFragment
import com.square.android.ui.fragment.map.MapFragment
import com.square.android.ui.fragment.profile.ProfileFragment
import com.square.android.ui.fragment.review.EXTRA_REDEMPTION
import com.square.android.utils.ActivityUtils
import com.square.android.utils.DialogDepository
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.bottom_view_map.view.*
import kotlinx.android.synthetic.main.notifications_badge.*
import kotlinx.android.synthetic.main.pending_congratulations.view.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import java.util.*

private const val REDEMPTIONS_POSITION = 1

class MainFabClickedEvent()

class NavFabClickedEvent()

class MainActivity : BaseActivity(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {

    val dialogDepository: DialogDepository by inject()

    @InjectPresenter
    lateinit var presenter: MainPresenter

    var canMainFabClick = true

    private val eventBus: EventBus by inject()

    override fun provideNavigator(): Navigator = MainNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
        }

        setContentView(R.layout.activity_main)

        pending_screen.btnFollow.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/square.app"))
            i.setPackage(getString(R.string.instagram_package))
            try {
                startActivity(i)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/square.app")))
            }
        }

        setUpNavigation()
        setUpNotifications()

        mainFab.setOnClickListener {
            if(canMainFabClick){
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        canMainFabClick = true

                        mainFab.tintFromRes(R.color.gray_back_arrow)
                    }
                }, 1700)
                canMainFabClick = false

                mainFab.tintFromRes(R.color.gray_btn_disabled)

                eventBus.post(MainFabClickedEvent())
            }
        }

        navFab.setOnClickListener {
            eventBus.post(NavFabClickedEvent())
        }
    }

    private fun setUpNotifications() {
        intent?.extras?.takeIf { it.size() > 0 }?.run {
            val pushType = getString("pushType")
            if (pushType != null) {
                val notifType = NotificationType.values()
                        .first{ it.notifName == pushType}
                dialogDepository.showDialogFromNotification(notifType, this)
            }
        }
    }

    fun hideMapBottomView(){
        bottomViewMapLl.visibility = View.GONE
    }

    fun setMapBottomBarContent(place: Place){
        val view = bottomViewMapLl.mapInfo

        val isEvent = place.event != null

        //TODO change to rv with adapter(images) later
        view.mapPlaceInfoImage.loadImage(place.mainImage ?: (place.photos?.firstOrNull() ?: ""))
        view.title.text = place.name

        view.mainContent.setOnClickListener {
            if(isEvent){
                eventBus.post(EventSelectedEvent(place))
            } else{
                eventBus.post(PlaceSelectedEvent(place))
            }
        }

        if(isEvent) {
            view.placeRatingLl.visibility = View.GONE

            view.space.setVisible(true)

            //TODO change ic drawable
            view.iconLl.setPadding(0, Math.round(resources.getDimension(R.dimen.v_12dp)), 0, 0)
            //TODO where to get it?
            view.icText.text = if(isEvent) "Icon text" else ""
            view.icText.visibility = View.VISIBLE

            view.eventPlaceNameTv.text = place.name
            view.eventPlaceNameLl.visibility = View.VISIBLE

            view.addressTv.text = place.address
            view.addressLl.visibility = View.VISIBLE

            view.distanceLl.visibility = View.GONE

            //TODO where to get it?
            view.dateTv.text = if(isEvent) "Date" else ""
            view.dateLl.visibility = View.VISIBLE

        } else {
            //TODO where to get it?
            view.placeRatingLl.placeRatingTv.text = if(!isEvent) "4.1" else ""
            view.placeRatingLl.visibility = View.VISIBLE

            view.space.setVisible(false)

            //TODO change ic drawable
            view.iconLl.setPadding(0, 0, 0, 0)
            view.icText.visibility = View.GONE

            view.eventPlaceNameLl.visibility = View.GONE

            view.addressTv.text = place.address
            view.addressLl.visibility = View.VISIBLE

            view.distanceTv.text = place.distance.asDistance()
            view.distanceLl.visibility = View.VISIBLE

            view.dateLl.visibility = View.GONE
        }

        bottomViewMapLl.visibility = View.VISIBLE
    }

    fun setUpMainFabImage(@DrawableRes iconRes: Int){
        mainFab.drawableFromRes(iconRes)
    }

    override fun checkInitial() {
        bottomNavigation.selectedItemId = R.id.action_explore
    }

    override fun showUserPending() {
        pending_screen.visibility = View.VISIBLE
    }

    override fun hideUserPending() {
        pending_screen.visibility = View.GONE
    }

    override fun setActiveRedemptions(count: Int) {
        when (count) {
            0 -> notificationsBadge.visibility = View.GONE
            in 1..9 -> {
                notificationsBadge.text = count.toString()
                notificationsBadge.visibility = View.VISIBLE
            }
            else -> {
                notificationsBadge.setText(R.string.badge_big_count)
                notificationsBadge.visibility = View.VISIBLE
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val screenKey = when (item.itemId) {
            R.id.action_explore -> SCREENS.EXPLORE
            
            R.id.action_agenda -> {
                setActiveRedemptions(0)
                SCREENS.AGENDA
            }

            //TODO
//            R.id.action_chat ->

            R.id.action_profile -> SCREENS.PROFILE
            else -> SCREENS.PROFILE
        }

        presenter.navigationClicked(screenKey)

        return true
    }

    private fun setUpNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(this)

        addBadgeView()
    }

    override fun onResume() {
        super.onResume()
        presenter.checkPending()
    }

    override fun onDestroy() {
        App.INSTANCE.mixpanel.flush()
        super.onDestroy()
    }

    private fun addBadgeView() {
        val bottomNavigationMenuView = bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(REDEMPTIONS_POSITION)
        val itemView = v as BottomNavigationItemView

        val inflater = LayoutInflater.from(this)

        inflater.inflate(R.layout.notifications_badge, itemView, true)
    }

    private class MainNavigator(var activity: FragmentActivity) : AppNavigator(activity, R.id.main_container) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {

                    SCREENS.START ->
                        context.intentFor<StartActivity>()

                    SCREENS.EVENT ->{
                        context.intentFor<EventActivity>(EXTRA_EVENT_ID to data as String)
                    }

                    SCREENS.PLACE ->{
                        val extras = data as PlaceExtras
                        context.intentFor<PlaceActivity>(PLACE_EXTRA_ID to extras.placeId, PLACE_EXTRA_DAY_SELECTED to extras.daySelectedPosition)
                    }

                    SCREENS.CAMPAIGN_DETAILS ->
                        context.intentFor<CampaignDetailsActivity>(CAMPAIGN_EXTRA_ID to data as Long)

                    SCREENS.SELECT_OFFER ->
                        context.intentFor<SelectOfferActivity>(EXTRA_REDEMPTION to data as RedemptionInfo)

                    SCREENS.GALLERY ->
                        context.intentFor<GalleryActivity>(USER_EXTRA to data as Profile.User)

                    SCREENS.NO_CONNECTION ->
                        context.intentFor<NoConnectionActivity>()

                    SCREENS.SUBSCRIPTION_ERROR ->
                        context.intentFor<SubscriptionErrorActivity>()

                    SCREENS.TUTORIAL_VIDEOS ->
                        context.intentFor<TutorialVideosActivity>()

                    //TODO:F no more pass eligible? - if yes - delete all data(ac, fragments/ layouts etc.) related to it
//                    SCREENS.PASS_ELIGIBLE -> {
//                        context.intentFor<PassEligibleActivity>(PASS_CAN_BACK_EXTRA to data as Boolean)
//                    }

                    ///// Settings
                    SCREENS.SOCIAL_CHANNELS -> {
                        context.intentFor<SocialChannelActivity>()
                    }

                    SCREENS.SPECIALITIES -> {
                        context.intentFor<SpecialitiesActivity>()
                    }

                    SCREENS.ACTIVE_PLAN -> {
                        val extras = data as ActivePlanExtras
                        context.intentFor<ActivePlanActivity>(CAN_BACK_EXTRA to extras.canGoBack, BILLING_TOKEN_EXTRA to extras.billingTokenInfo)
                    }

                    SCREENS.EARN_MORE_CREDITS ->
                        context.intentFor<EarnMoreCreditsActivity>()

                    SCREENS.SETTINGS ->
                        context.intentFor<SettingsActivity>(com.square.android.ui.activity.settings.USER_EXTRA to data as Profile.User)
                    ////

                    SCREENS.CAMPAIGN_FINISHED ->
                        context.intentFor<CampaignDetailsActivity>(CAMPAIGN_EXTRA_ID to data as Long)

                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?): Fragment? = when (screenKey) {
            SCREENS.EXPLORE -> {
                (activity as MainActivity).setUpMainFabImage(R.drawable.r_pin)
                (activity as MainActivity).mainFab.show()

                (activity as MainActivity).hideMapBottomView()

                ExploreFragment()
            }

            SCREENS.AGENDA -> {
                (activity as MainActivity).mainFab.hide()
                (activity as MainActivity).navFab.hide()
                (activity as MainActivity).hideMapBottomView()
                AgendaFragment()
            }

            SCREENS.PROFILE -> {
                (activity as MainActivity).mainFab.hide()
                (activity as MainActivity).navFab.hide()
                (activity as MainActivity).hideMapBottomView()
                ProfileFragment()
            }

            SCREENS.EDIT_PROFILE -> {
                (activity as MainActivity).mainFab.hide()
                (activity as MainActivity).navFab.hide()
                (activity as MainActivity).hideMapBottomView()
                EditProfileFragment()
            }

            SCREENS.SEARCH -> {
                (activity as MainActivity).mainFab.hide()
                (activity as MainActivity).navFab.hide()
                (activity as MainActivity).hideMapBottomView()
                SearchFragment(data as Int)
            }

            SCREENS.MAP -> {
                (activity as MainActivity).mainFab.show()
                (activity as MainActivity).setUpMainFabImage(R.drawable.ic_list)
                (activity as MainActivity).navFab.show()
                MapFragment(data as MutableList<Place>)
            }

            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out)
        }
    }
}

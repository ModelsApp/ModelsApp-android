package com.square.android.presentation.presenter.profile

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.crashlytics.android.Crashlytics
import com.square.android.GOOGLEBILLING
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.BillingSubscription
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileSocialView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

const val SUBSCRIPTION_NORMAL = 1
const val SUBSCRIPTION_PREMIUM = 2

const val SUBSCRIPTION_TYPE_WEEKLY = 1
const val SUBSCRIPTION_TYPE_MONTHLY = 2
const val SUBSCRIPTION_TYPE_NO_LIMIT = 9

class ProfileUpdatedEvent

@InjectViewState
class ProfileSocialPresenter: BasePresenter<ProfileSocialView>(){

    private val eventBus: EventBus by inject()

    var actualTokenInfo: BillingTokenInfo? = null

    init {
        eventBus.register(this)

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProfileUpdatedEvent(event: ProfileUpdatedEvent) {
        loadData()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        val user = repository.getCurrentUser().await()

        loadSubscriptions(user)
    }

    private fun loadSubscriptions(user: Profile.User) = launch ({
        Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions()")

        Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions()")

        actualTokenInfo = null

        val isPaymentRequired = repository.getUserInfo().isPaymentRequired

        if(isPaymentRequired){
            Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> PAYMENT REQUIRED")
            Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> PAYMENT REQUIRED")

            val subscriptions: MutableList<BillingSubscription> = mutableListOf()

            val billings: List<BillingTokenInfo> = repository.getPaymentTokens().await()

            for (billing in billings) {
                val data = billingRepository.getSubscription(billing.subscriptionId!!, billing.token!!).await()

                data?.let {
                    it.subscriptionId = billing.subscriptionId
                    it.token = billing.token
                    subscriptions.add(it) }
            }

            Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> subscriptionsList: ${subscriptions.toString()}")
            Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> subscriptionsList: ${subscriptions.toString()}")

            //TODO change to actual time from API
            val actualTimeInMillis: Long = Calendar.getInstance().timeInMillis

            val perWeekValidSub = subscriptions.filter { it.subscriptionId == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
            perWeekValidSub?.let {
                Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perWeekValidSub NOT NULL")
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perWeekValidSub NOT NULL")

                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                if(validExpiry){
                    actualTokenInfo = BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token }
                    actualTokenInfo!!.subscriptionType = SUBSCRIPTION_NORMAL
                    actualTokenInfo!!.planType = SUBSCRIPTION_TYPE_WEEKLY
                }

            } ?: run {
                Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perWeekValidSub IS NULL")
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perWeekValidSub IS NULL")
            }

            val perMonthValidSub = subscriptions.filter { it.subscriptionId == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
            perMonthValidSub?.let {
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perMonthValidSub NOT NULL"))
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perMonthValidSub NOT NULL")

                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                if(validExpiry){
                    actualTokenInfo = BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token }
                    actualTokenInfo!!.subscriptionType = SUBSCRIPTION_NORMAL
                    actualTokenInfo!!.planType = SUBSCRIPTION_TYPE_MONTHLY
                }

            } ?: run {
                Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perMonthValidSub IS NULL")
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perMonthValidSub IS NULL")
            }

            val perWeekPremiumValidSub = subscriptions.filter { it.subscriptionId == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_PREMIUM_NAME}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
            perWeekPremiumValidSub?.let {
                Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perWeekPremiumValidSub NOT NULL")
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perWeekPremiumValidSub NOT NULL")

                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                if(validExpiry){
                    actualTokenInfo = BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token }
                    actualTokenInfo!!.subscriptionType = SUBSCRIPTION_PREMIUM
                    actualTokenInfo!!.planType = SUBSCRIPTION_TYPE_WEEKLY
                }

            } ?: run {
                Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perWeekPremiumValidSub IS NULL")
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perWeekPremiumValidSub IS NULL")
            }

            val perMonthPremiumValidSub = subscriptions.filter { it.subscriptionId == GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_PREMIUM_NAME}.sortedByDescending {it.expiryTimeMillis}.firstOrNull()
            perMonthPremiumValidSub?.let {
                Crashlytics.logException(Throwable("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perMonthPremiumValidSub NOT NULL"))
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perMonthPremiumValidSub NOT NULL")

                val validExpiry = (it.expiryTimeMillis - actualTimeInMillis) > 1000

                if(validExpiry){
                    actualTokenInfo = BillingTokenInfo().apply { subscriptionId = it.subscriptionId; token = it.token }
                    actualTokenInfo!!.subscriptionType = SUBSCRIPTION_PREMIUM
                    actualTokenInfo!!.planType = SUBSCRIPTION_TYPE_MONTHLY
                }

            } ?: run {
                Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> perMonthPremiumValidSub IS NULL")
                Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> perMonthPremiumValidSub IS NULL")
            }

        } else{
            actualTokenInfo = BillingTokenInfo()
            actualTokenInfo!!.subscriptionType = SUBSCRIPTION_NORMAL
            actualTokenInfo!!.planType = SUBSCRIPTION_TYPE_NO_LIMIT

            Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> PAYMENT NOT REQUIRED")
            Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> PAYMENT NOT REQUIRED")
        }

        actualTokenInfo?.let {
            viewState.showData(user, it)
        } ?: run {
            viewState.showMessage(R.string.error_occurred)
        }

        viewState.hideProgress()

    } ,{ error ->

        viewState.showMessage(R.string.error_occurred)
        viewState.hideProgress()

        Crashlytics.log("SUBSCRIPTIONS -> ProfilePresenter: loadSubscriptions() -> error: ${error.toString()}")
        Log.d("SUBSCRIPTIONS","ProfilePresenter: loadSubscriptions() -> error: ${error.toString()}")
    })

    fun navigateTutorialVideos(){
        router.navigateTo(SCREENS.TUTORIAL_VIDEOS)
    }

    fun navigateTerms(){
        //TODO
    }

    fun navigatePrivacy(){
        //TODO
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

}
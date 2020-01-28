package com.square.android.ui.activity.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.crashlytics.android.Crashlytics
import com.square.android.GOOGLEBILLING
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.presentation.presenter.profile.ActivePlanPresenter
import com.square.android.presentation.view.profile.ActivePlanView
import com.square.android.ui.activity.BaseBillingActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_active_plan.*
import ru.terrakok.cicerone.Navigator

const val CAN_BACK_EXTRA = "CAN_BACK_EXTRA"
const val BILLING_TOKEN_EXTRA = "BILLING_TOKEN_EXTRA"

class ActivePlanExtras(val canGoBack: Boolean, val billingTokenInfo: BillingTokenInfo)

class ActivePlanActivity: BaseBillingActivity(), ActivePlanView{

    @InjectPresenter
    lateinit var presenter: ActivePlanPresenter

    @ProvidePresenter
    fun providePresenter() = ActivePlanPresenter(intent.getBooleanExtra(CAN_BACK_EXTRA, false), intent.getParcelableExtra(BILLING_TOKEN_EXTRA))

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var productLoaded = false

    private var skuDetailsList: List<SkuDetails>? = null

    private var selectedSkuDetails: SkuDetails? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_active_plan)

        loadingDialog = LoadingDialog(this)

        btnSwitch.setOnClickListener {
            if (!presenter.userHasPremiumWeekSub) {
                if (subscriptionsSupported(billingClient)) {
                    if (checkBillingReady()) {
                        selectedSkuDetails?.let {
                            val flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(it)
                                    .build()
                            billingClient.launchBillingFlow(this, flowParams)
                        }
                    }
                } else {
                    Crashlytics.log("BILLING -> PassEligibleActivity: subscriptions not supported")

                    Log.d("BILLING", "subscriptions not supported")

                    showMessage(getString(R.string.subscriptions_not_supported))
                }
            } else {
                //TODO:A cancel subscription
            }
        }

        arrowBack.setOnClickListener { onBackPressed() }

        clPremium.setOnClickListener {
            if(!clPremium.isChecked) {
                clPremium.isChecked = true

                clBasic.isChecked = false

                switchLl.visibility = if(!presenter.userHasPremiumWeekSub) View.VISIBLE else View.GONE
            }
        }

        clBasic.setOnClickListener {
            if(!clBasic.isChecked) {
                clBasic.isChecked = true

                clPremium.isChecked = false

                switchLl.visibility = if(!presenter.userHasPremiumWeekSub) View.GONE else View.VISIBLE
            }
        }

        connectBilling()
    }

    override fun onBillingConnected() {
        if(!productLoaded){
            getProductDetails()
        }
    }

    private fun getProductDetails(){
        val skuList = ArrayList<String>()
        skuList.add(GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                this.skuDetailsList = skuDetailsList

                selectedSkuDetails = skuDetailsList!!.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME }

                productLoaded = true

                showContent()

                premiumPrice.text = getString(R.string.price_subscription_week_format, skuDetailsList.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME }?.price )
            } else {
                Crashlytics.log("BILLING -> ActivePlanActivity: querySkuDetailsAsync() | responseCode != OK  or skuDetailsList == null\"")

                Log.d("BILLING","| ActivePlanActivity: querySkuDetailsAsync() | responseCode != OK  or skuDetailsList == null")
            }
        }
    }

    private fun showContent(){
        progress.visibility = View.GONE

        descTv.visibility = View.VISIBLE

        if(presenter.userHasPremiumWeekSub){
            clPremium.isChecked = true
            premium_rb.visibility = View.GONE
            premiumCurrentTv.visibility = View.VISIBLE

            btnText.text = getString(R.string.cancel_subscription)
            btnPrice.text = getString(R.string.free)

            basic_rb.visibility = View.VISIBLE
        } else{
            clBasic.isChecked = true
            basic_rb.visibility = View.GONE
            basicCurrentTv.visibility = View.VISIBLE

            btnText.text = getString(R.string.switch_to_premium)
            btnPrice.text = skuDetailsList?.firstOrNull { it.sku == GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME }?.price

            premium_rb.visibility = View.VISIBLE
        }

        clPremium.visibility = View.VISIBLE
        clBasic.visibility = View.VISIBLE
    }

    override fun handlePurchases(nullOrEmpty: Boolean) {
        if(nullOrEmpty){
            showMessage(getString(R.string.something_went_wrong))
        } else{
            showDialog()
        }
    }

    override fun purchasesComplete() {
        hideDialog()

        showMessage(getString(R.string.purchase_completed_successfully))

        //TODO:A in profileFragment -> onStart -> update active plan item(or all items?)
        finish()
    }

    private fun subscriptionsSupported(client: BillingClient): Boolean =
            client.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode == BillingClient.BillingResponseCode.OK

    override fun onBackPressed() {
        if (presenter.canGoBack) {
            super.onBackPressed()
        }
    }

    override fun hideDialog() {
        loadingDialog?.dismiss()
    }

    override fun showDialog() {
        loadingDialog?.show()
    }

}
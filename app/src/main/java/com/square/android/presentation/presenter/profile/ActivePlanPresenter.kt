package com.square.android.presentation.presenter.profile

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.android.billingclient.api.Purchase
import com.arellomobile.mvp.InjectViewState
//import com.crashlytics.android.Crashlytics
import com.square.android.GOOGLEBILLING
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.TokenInfo
import com.square.android.di.PurchasesUpdatedEvent
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ActivePlanView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.lang.Exception
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

@InjectViewState
class ActivePlanPresenter(var canGoBack: Boolean = true, val actualTokenInfo: BillingTokenInfo): BasePresenter<ActivePlanView>(){

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    var userHasPremiumWeekSub = false

    init {
        //TODO:A subscriptions logic will be changed -> for now, normal and premium are payed subs. It all means different things now
        userHasPremiumWeekSub = actualTokenInfo.subscriptionType == SUBSCRIPTION_PREMIUM
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPurchasesUpdatedEvent(event: PurchasesUpdatedEvent) = launch ({

//        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent()")

        val purchases = event.data

        viewState.handlePurchases(purchases.isNullOrEmpty())

        purchases?.let {

            val verifiedList: MutableList<Boolean> = mutableListOf()

            for (purchase in purchases) {
                val verified = verifyPurchase(purchase.originalJson , purchase.signature)

                if(!verified){
//                    Crashlytics.log("PURCHASE -> ActivePlanPresenter: verifyPurchase() -> NOT VERIFIED, id: ${purchase.sku}")

                    Log.d("PURCHASE","| ActivePlanPresenter: verifyPurchase() -> NOT VERIFIED")
                } else{
//                    Crashlytics.log("PURCHASE -> ActivePlanPresenter: verifyPurchase() -> VERIFIED SUCCESSFULLY, id: ${purchase.sku}")
                }

                verifiedList.add(verified)
            }

//            Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> purchases: ${purchases.toString()}")

            var listPos = -1
            for (purchase in purchases) {
                listPos++

                if(verifiedList[listPos]){
                    repository.sendPaymentToken(BillingTokenInfo().apply { subscriptionId = purchase.sku; token = purchase.purchaseToken }).await()

//                    Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> sending payment token: subscriptionId = " +
//                            "${purchase.sku}, token = ${purchase.purchaseToken}")
                }
            }

            listPos = -1
            for (purchase in purchases) {
                listPos++

                if(verifiedList[listPos]){
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        repository.setUserEntitlement(purchase.sku, true)

//                        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> setting user's entitlement: ${purchase.sku}")
                    } else{
//                        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> cannot set user's entitlement, " +
//                                "purchase.purchaseState == ${purchase.purchaseState}")
                    }
                }
            }

            listPos = -1
            for (purchase in purchases) {
                listPos++

                if(verifiedList[listPos]){
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED")

                        billingRepository.acknowledgeSubscription(purchase.sku, purchase.purchaseToken, TokenInfo().apply { payload = "" }).await()

//                        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> acknowledgeSubscription")

                    } else{
//                        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> purchase.purchaseState == ${purchase.purchaseState.toString()}")
                    }
                }
            }

//            Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> PURCHASE COMPLETED SUCCESSFULLY")
            viewState.purchasesComplete()
        } // ?: Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> purchases ARE NULL")

    }, { error ->
        //TODO must handle errors here - mixing repository and billingRepository

        viewState.hideDialog()

//        Crashlytics.log("PURCHASE -> ActivePlanPresenter: onPurchasesUpdatedEvent() -> error: ${error.toString()}")
        Log.d("PURCHASE","ActivePlanPresenter: onPurchasesUpdatedEvent() -> error: ${error.toString()}")
    })

    private fun verifyPurchase(signedData: String, signature: String): Boolean {

        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(signature)) {
//            Crashlytics.log("PURCHASE -> ActivePlanPresenter: verifyPurchase() - Purchase verification failed: missing data")
            Log.e("PURCHASE", "ActivePlanPresenter: verifyPurchase() - Purchase verification failed: missing data")
            return false
        }

        try {
            val x509publicKey = X509EncodedKeySpec(Base64.decode(GOOGLEBILLING.APP_PUBLIC_KEY.toByteArray(), Base64.DEFAULT))
            val publicKey = KeyFactory.getInstance("SHA256withRSA").generatePublic(x509publicKey)

            val rsaVerify = Signature.getInstance("SHA256withRSA") // ,provider = "BC" ?
            rsaVerify.initVerify(publicKey)
            rsaVerify.update(signedData.toByte())
            return rsaVerify.verify(signature.toByteArray())

        } catch (e: Exception){
//            Crashlytics.log("PURCHASE -> ActivePlanPresenter: verifyPurchase() - Exception: ${e.toString()}")
            Log.e("PURCHASE", "ActivePlanPresenter: verifyPurchase() - Exception: ${e.toString()}")
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
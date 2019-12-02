package com.square.android.ui.activity.selectOffer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.selectOffer.SelectOfferPresenter
import com.square.android.presentation.view.selectOffer.SelectOfferView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.fragment.claimedRedemption.ClaimedRedemptionFragment
import com.square.android.ui.fragment.offersList.OffersListFragment
import com.square.android.ui.fragment.review.EXTRA_REDEMPTION
import com.square.android.ui.fragment.reviewUpload.PhotoResultEvent
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

class SelectOfferActivity: BaseActivity(), SelectOfferView {

    @InjectPresenter
    lateinit var presenter: SelectOfferPresenter

    @ProvidePresenter
    fun providePresenter() = SelectOfferPresenter( intent.getParcelableExtra(EXTRA_REDEMPTION) as RedemptionInfo)

    private val eventBus: EventBus by inject()

    override fun provideNavigator(): Navigator = SelectOfferNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_offer)
    }

    private class SelectOfferNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.selectOfferContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) = null

        override fun createFragment(screenKey: String, data: Any?): Fragment? = when (screenKey) {

            SCREENS.OFFERS_LIST -> {OffersListFragment.newInstance(data as RedemptionInfo) }

            SCREENS.CLAIMED_REDEMPTION -> {
                ClaimedRedemptionFragment.newInstance(data as RedemptionInfo)
            }

            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if(command is Forward){
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else{
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val uri: Uri? = when (requestCode) {
            IMAGE_PICKER_RC -> data?.data
            else -> data?.extras?.get("data") as Uri? ?: FileUtils.getOutputFileUri(this)
        }

        uri?.let {eventBus.post(PhotoResultEvent(it))}
    }
}

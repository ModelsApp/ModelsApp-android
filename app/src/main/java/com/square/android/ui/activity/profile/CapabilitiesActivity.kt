package com.square.android.ui.activity.profile

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.profile.CapabilitiesPresenter
import com.square.android.presentation.view.profile.CapabilitiesView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import ru.terrakok.cicerone.Navigator

class CapabilitiesActivity: BaseActivity(), CapabilitiesView {


    //TODO waiting for api


    @InjectPresenter
    lateinit var presenter: CapabilitiesPresenter

    @ProvidePresenter
    fun providePresenter() = CapabilitiesPresenter()

    private var loadingDialog: LoadingDialog? = null

    lateinit var adapter: CapabilityAdapter

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_capabilities)

        loadingDialog = LoadingDialog(this)
    }

//    override fun showData(data: List<UserSocialChannel>){
//        adapter = SocialChannelsAdapter(data, object: SocialChannelsAdapter.Handler{
//            override fun itemClicked(position: Int) {
//
//                presenter.itemClicked(position)
//            }
//        })
//
//        rvSocialChannels.adapter = adapter
//        rvSocialChannels.layoutManager = LinearLayoutManager(rvSocialChannels.context, RecyclerView.VERTICAL,false)
//    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

}
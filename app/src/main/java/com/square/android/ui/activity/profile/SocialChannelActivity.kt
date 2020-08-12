package com.square.android.ui.activity.profile

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.UserSocialChannel
import com.square.android.extensions.setVisible
import com.square.android.extensions.textIsEmpty
import com.square.android.presentation.presenter.profile.SocialChannelsPresenter
import com.square.android.presentation.view.profile.SocialChannelsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_social_channels.*
import ru.terrakok.cicerone.Navigator

class SocialChannelActivity: BaseActivity(), SocialChannelsView {

    @InjectPresenter
    lateinit var presenter: SocialChannelsPresenter

    @ProvidePresenter
    fun providePresenter() = SocialChannelsPresenter()

    private var loadingDialog: LoadingDialog? = null

    lateinit var adapter: SocialChannelsAdapter

    private var socialDialog: SocialChannelDialog? = null

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        socialDialog = SocialChannelDialog(this)

        loadingDialog = LoadingDialog(this)

        setContentView(R.layout.activity_social_channels)
    }

    override fun showData(data: List<UserSocialChannel>){
        adapter = SocialChannelsAdapter(data, object: SocialChannelsAdapter.Handler{
            override fun itemClicked(position: Int) {

                presenter.itemClicked(position)
            }
        })

        rvSocialChannels.adapter = adapter
        rvSocialChannels.layoutManager = LinearLayoutManager(rvSocialChannels.context, RecyclerView.VERTICAL,false)
    }

    override fun showSocialDialog(item: UserSocialChannel){
        socialDialog?.show(item) { newName ->

            val accountName = item.userChannel?.accountName ?: ""

            if(newName.textIsEmpty() && !accountName.textIsEmpty()){
                presenter.deleteSocialAccount(item)
            } else if(newName != accountName){
                presenter.addSocialAccount(item, newName)
            }
        }
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() {
        rvSocialChannels.setVisible(false)
        progressBar.setVisible(true)
    }

    override fun hideProgress() {
        progressBar.setVisible(false)
        rvSocialChannels.setVisible(true)
    }

}
package com.square.android.ui.activity.profile

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.UserCapability
import com.square.android.extensions.setVisible
import com.square.android.presentation.presenter.profile.CapabilitiesPresenter
import com.square.android.presentation.view.profile.CapabilitiesView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_capabilities.*
import ru.terrakok.cicerone.Navigator

class CapabilitiesActivity: BaseActivity(), CapabilitiesView {

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

        saveTv.setOnClickListener {
            presenter.saveClicked()
            saveTv.isEnabled = false
        }

        loadingDialog = LoadingDialog(this)

        capabilitiesArrowBack.setOnClickListener { onBackPressed() }
    }

    override fun showData(data: List<UserCapability>, selectedItems: MutableList<String>){
        adapter = CapabilityAdapter(data, selectedItems, object: CapabilityAdapter.Handler{
            override fun itemClicked(position: Int) {
                presenter.itemClicked(position)
                adapter.notifyChanged(position)

                saveTv.isEnabled = presenter.selectedItems.isNotEmpty()
            }
        })

        rvCapabilities.adapter = adapter
        rvCapabilities.layoutManager = LinearLayoutManager(rvCapabilities.context, RecyclerView.VERTICAL,false)
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() {
        rvCapabilities.setVisible(false)
        progressBar.setVisible(true)
    }

    override fun hideProgress() {
        progressBar.setVisible(false)
        rvCapabilities.setVisible(true)
    }

}
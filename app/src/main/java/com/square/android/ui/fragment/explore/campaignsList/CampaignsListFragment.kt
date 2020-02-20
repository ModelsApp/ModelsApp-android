package com.square.android.ui.fragment.explore.campaignsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.CampaignInfo
import com.square.android.presentation.presenter.explore.CampaignsPresenter
import com.square.android.presentation.view.campaigns.CampaignsView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_campaigns_list.*
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaModifierListOwner.DefaultImpls.getVisibility
import android.R



const val CAMPAIGN_EXTRA_ID = "CAMPAIGN_EXTRA_ID"
class CampaignsListFragment(var data: MutableList<CampaignInfo>): BaseFragment(), CampaignsView, CampaignsAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: CampaignsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignsPresenter(data)

    private var adapter: CampaignsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_campaigns_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        campaignsList.setHasFixedSize(true)



//        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0 && mFloatingActionButton.getVisibility() === View.VISIBLE) {
//                    mFloatingActionButton.hide()
//                } else if (dy < 0 && mFloatingActionButton.getVisibility() !== View.VISIBLE) {
//                    mFloatingActionButton.show()
//                }
//            }
//        })

    }

    override fun showData(data: List<CampaignInfo>) {
        adapter = CampaignsAdapter(data, this)
        campaignsList.adapter = adapter
        campaignsList.layoutManager = LinearLayoutManager(campaignsList.context, RecyclerView.VERTICAL,false)
        campaignsList.addItemDecoration(MarginItemDecorator(campaignsList.context.resources.getDimension(R.dimen.v_12dp).toInt(), true))
    }

    override fun updateCampaigns(data: List<CampaignInfo>) {
        adapter = CampaignsAdapter(data, this)
        campaignsList.adapter = adapter
    }

    override fun itemClicked(campaignInfo: CampaignInfo) {
        presenter.itemClicked(campaignInfo)
    }
}
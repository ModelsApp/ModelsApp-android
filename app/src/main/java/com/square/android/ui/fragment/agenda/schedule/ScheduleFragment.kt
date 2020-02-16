package com.square.android.ui.fragment.agenda.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.SchedulePresenter
import com.square.android.presentation.view.agenda.ScheduleView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment: BaseFragment(), ScheduleView, ScheduleAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: SchedulePresenter

    @ProvidePresenter
    fun providePresenter() = SchedulePresenter()

    private var adapter : ScheduleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleList.setHasFixedSize(true)
    }

    override fun showData(ordered: List<Any>) {
        adapter = ScheduleAdapter(ordered, this)
        scheduleList.adapter = adapter
        scheduleList.layoutManager = LinearLayoutManager(scheduleList.context, RecyclerView.VERTICAL,false)
    }

    override fun showProgress() {
        scheduleList.visibility = View.INVISIBLE
        scheduleProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        scheduleProgress.visibility = View.GONE
        scheduleList.visibility = View.VISIBLE
    }


    //TODO header are wrong - all items are grouped as PAST
    //TODO in KotlinExtensions -> Calendar.relativeTimeString

    //TODO item clicks from adapter


    override fun cancelCampaignClicked(id: Long) {
//        presenter.cancelCampaignClicked(id)
    }

    override fun cancelRedemptionClicked(id: Long) {
//        presenter.cancelRedemptionClicked(id)
    }

    override fun campaignItemClicked(id: Long) {
        presenter.campaignClicked(id)
    }

    override fun claimClicked(id: Long) {
        presenter.claimClicked(id)
    }

    override fun claimedItemClicked(id: Long) {
        presenter.claimedInfoClicked(id)
    }

    override fun redemptionDetailsClicked(placeId: Long) {
        presenter.redemptionDetailsClicked(placeId)
    }

//    override fun removeItem(position: Int) {
//        adapter?.removeItem(position)
//    }

}
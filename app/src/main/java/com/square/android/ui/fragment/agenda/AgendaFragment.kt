package com.square.android.ui.fragment.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.AgendaPresenter
import com.square.android.presentation.view.agenda.AgendaView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_agenda.*

class AgendaFragment: BaseFragment(), AgendaView {

    @InjectPresenter
    lateinit var presenter: AgendaPresenter

    @ProvidePresenter
    fun providePresenter() = AgendaPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPager()
    }

    private fun setUpPager() {
        agendaPager.isPagingEnabled = true
        agendaPager.adapter = AgendaFragmentAdapter(childFragmentManager)
        agendaPager.offscreenPageLimit = 4
        agendaTabs.setupWithViewPager(agendaPager)

        agendaPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                presenter.tabClicked(position)
            }
        })
    }

    override fun showProgress() {
        agendaPager.visibility = View.INVISIBLE
        agendaProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        agendaProgress.visibility = View.GONE
        agendaPager.visibility = View.VISIBLE
    }

}
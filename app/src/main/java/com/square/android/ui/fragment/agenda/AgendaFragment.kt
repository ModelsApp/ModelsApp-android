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
import com.square.android.ui.dialogs.AgendaInfoDialog
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_agenda.*
import java.util.*

class AgendaFragment: BaseFragment(), AgendaView {

    @InjectPresenter
    lateinit var presenter: AgendaPresenter

    @ProvidePresenter
    fun providePresenter() = AgendaPresenter()

    private var dialog: AgendaInfoDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        agendaDate.text = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).capitalize() + " " + calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR)

        setUpPager()

        dialog = AgendaInfoDialog(activity!!)

        icInfo.setOnClickListener {
            dialog!!.show {  }
        }
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

}
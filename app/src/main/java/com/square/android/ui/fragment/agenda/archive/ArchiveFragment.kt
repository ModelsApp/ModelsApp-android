package com.square.android.ui.fragment.agenda.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.ArchivePresenter
import com.square.android.presentation.view.agenda.ArchiveView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.agenda.calendar.CalendarEvent
import kotlinx.android.synthetic.main.fragment_archive.*

class ArchiveFragment: BaseFragment(), ArchiveView{

    @InjectPresenter
    lateinit var presenter: ArchivePresenter

    @ProvidePresenter
    fun providePresenter() = ArchivePresenter()

    private var adapter: ArchiveAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_archive, container, false)
    }

    override fun showData(items: List<CalendarEvent>) {
        adapter = ArchiveAdapter(items, object: ArchiveAdapter.Handler{
            override fun archiveItemClicked(position: Int) {
                //TODO
            }
        })

        archiveList.adapter = adapter
        archiveList.layoutManager = LinearLayoutManager(archiveList.context, RecyclerView.VERTICAL,false)
    }

    override fun showProgress() {
        archiveList.visibility = View.INVISIBLE
        archiveProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        archiveProgress.visibility = View.GONE
        archiveList.visibility = View.VISIBLE
    }
}
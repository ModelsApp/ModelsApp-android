package com.square.android.ui.fragment.mainLists.eventsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.mainLists.EventsListPresenter
import com.square.android.presentation.view.mainLists.EventsListView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_events_list.*

class EventsListFragment(var data: MutableList<Place>): BaseFragment(), EventsListView, EventsAdapter.Handler{

    @InjectPresenter
    lateinit var presenter: EventsListPresenter

    @ProvidePresenter
    fun providePresenter() = EventsListPresenter(data)

    private var adapter: EventsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsList.setHasFixedSize(true)
    }

    override fun showData(data: List<Place>) {
        adapter = EventsAdapter(data, this)
        eventsList.adapter = adapter
        eventsList.layoutManager = LinearLayoutManager(eventsList.context, RecyclerView.VERTICAL,false)
        eventsList.addItemDecoration(MarginItemDecorator(eventsList.context.resources.getDimension(R.dimen.v_12dp).toInt(), true))
    }

    override fun updateEvents(data: List<Place>) {
        adapter = EventsAdapter(data, this)
        eventsList.adapter = adapter
    }

    override fun itemClicked(place: Place) {
        presenter.itemClicked(place)
    }
}
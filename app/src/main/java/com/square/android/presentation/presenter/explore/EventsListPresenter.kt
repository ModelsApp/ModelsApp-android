package com.square.android.presentation.presenter.explore

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.explore.EventsListView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class EventsUpdatedEvent(val data: MutableList<Place>)

@InjectViewState
class EventsListPresenter(var data: MutableList<Place>) : BasePresenter<EventsListView>() {

    private val eventBus: EventBus by inject()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventsUpdatedEvent(event: EventsUpdatedEvent) {
        data = event.data

        viewState.updateEvents(data.toList())
    }

    init {
        eventBus.register(this)

        viewState.showData(data.toList())
    }

    fun itemClicked(place: Place) {
        eventBus.post(EventSelectedEvent(place))
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

}
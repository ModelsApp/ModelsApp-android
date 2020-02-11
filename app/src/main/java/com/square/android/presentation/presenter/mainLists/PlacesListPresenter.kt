package com.square.android.presentation.presenter.mainLists

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.mainLists.PlacesListView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class PlacesUpdatedEvent(val data: MutableList<Place>)

@InjectViewState
class PlacesListPresenter(var data: MutableList<Place>) : BasePresenter<PlacesListView>() {

    private val eventBus: EventBus by inject()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlacesUpdatedEvent(event: PlacesUpdatedEvent) {
        data = event.data

        viewState.updatePlaces(data.toList())
    }

    init {
        eventBus.register(this)

        viewState.showData(data.toList())
    }

    fun itemClicked(place: Place) {
        eventBus.post(PlaceSelectedEvent(place))
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

}

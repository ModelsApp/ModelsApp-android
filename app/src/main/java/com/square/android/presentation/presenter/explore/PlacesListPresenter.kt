package com.square.android.presentation.presenter.explore

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.newPojo.NewPlace
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.explore.PlacesListView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class PlacesUpdatedEvent(val data: MutableList<NewPlace>)

@InjectViewState
class PlacesListPresenter(var data: MutableList<NewPlace>) : BasePresenter<PlacesListView>() {

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

    fun itemClicked(place: NewPlace) {
        eventBus.post(PlaceSelectedEvent(place))
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

}

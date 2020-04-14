package com.square.android.ui.fragment.agenda.calendar

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.CalendarPresenter
import com.square.android.presentation.view.agenda.CalendarView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

enum class EventType{
    TYPE_OFFER,
    TYPE_EVENT,
    TYPE_JOB,
    TYPE_CASTING
}


//TODO make day text bold on actual day oh the month
@Parcelize
data class CalendarItem(
        var day: String,
        var isCurrentMonth: Boolean,
        var events: List<CalendarEvent> = listOf()
): Parcelable

@Parcelize
data class CalendarEvent(
        var type: EventType,
        var title: String,
        var image: String,
        var interval: String,
        var address: String = "",
        var name: String = "",
        var idLong: Long = 0,
        var idString: String = ""
): Parcelable

class CalendarFragment: BaseFragment(), CalendarView {

    @InjectPresenter
    lateinit var presenter: CalendarPresenter

    lateinit var calendarAdapter: CalendarAdapter

    @ProvidePresenter
    fun providePresenter() = CalendarPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarList.setHasFixedSize(true)
    }

    override fun showData(items: List<CalendarItem>) {
        calendarAdapter = CalendarAdapter(items, object: CalendarAdapter.Handler{
            override fun calendarItemClicked(position: Int) {
                calendarAdapter.setSelectedItem(position)
                presenter.calendarItemClicked(position)
            }

        })

        rv_calendar.layoutManager = GridLayoutManager(activity, 7)
        rv_calendar.adapter = calendarAdapter
    }


    override fun showProgress() {
        //TODO
    }

    override fun hideProgress() {
        //TODO
    }

}
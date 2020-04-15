package com.square.android.ui.fragment.agenda

import com.square.android.App
import com.square.android.R
import androidx.fragment.app.FragmentManager
import com.square.android.presentation.presenter.agenda.POSITION_CALENDAR
import com.square.android.presentation.presenter.agenda.POSITION_SCHEDULE
import com.square.android.presentation.presenter.agenda.POSITION_TODO
import com.square.android.ui.fragment.agenda.calendar.CalendarFragment
import com.square.android.ui.fragment.agenda.schedule.ScheduleFragment
import com.square.android.ui.fragment.agenda.todo.ToDoFragment

//TODO will be 4
private const val ITEM_COUNT = 3

private var PAGE_TITLES_RES = listOf(R.string.calendar, R.string.schedule, R.string.to_do)

class AgendaFragmentAdapter(fragmentManager: FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_CALENDAR -> CalendarFragment()
            POSITION_SCHEDULE -> ScheduleFragment()
            POSITION_TODO -> ToDoFragment()
            //TODO To do + Archive
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT

    override fun getPageTitle(position: Int) = titles[position]
}
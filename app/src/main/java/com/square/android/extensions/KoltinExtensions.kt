package com.square.android.extensions

import android.util.Patterns
import com.square.android.App
import com.square.android.R
import java.text.SimpleDateFormat
import java.util.*

private val FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun Int.toOrdinalString() =
        this.toString() + when (this % 10) {
            in 11..13 -> "th"
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }

//TODO check date formatting in RedemptionInfo.date and CampaignBooking.pickUpDate and use one of the below methods to format it(RedemptionsPresenter, RedemptionsAdapter)
fun String.toDate(): Date {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    return format.parse(this)
}
fun String.toDateYMD(): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return format.parse(this)
}

fun Calendar.relativeTimeString(now: Calendar) : String {
    if (now.isToday(this)) return App.getString(R.string.today)

    val difference = timeInMillis - now.timeInMillis
    if (difference < 0) return App.getString(R.string.past)

    if (now.isTomorrow(this)) return App.getString(R.string.tomorrow)

    return getStringDate()
}

fun Calendar.isToday(other: Calendar): Boolean {
    return get(Calendar.DATE) == other.get(Calendar.DATE)
}

fun Calendar.isTomorrow(other: Calendar): Boolean {
    val futureCal: Calendar = Calendar.getInstance()
    futureCal.timeInMillis = timeInMillis
    futureCal.add(Calendar.DAY_OF_YEAR, 1)

    return futureCal.get(Calendar.YEAR) == other.get(Calendar.YEAR) && futureCal.get(Calendar.MONTH) == other.get(Calendar.MONTH) && futureCal.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}

fun String?.isUrl() : Boolean {
    return this != null && Patterns.WEB_URL.matcher(this).matches()
}

fun Calendar.getStringDate(): String {
    return FORMAT.format(time)
}


package nerd.tuxmobil.fahrplan.congress.schedule

import info.metadude.android.eventfahrplan.commons.temporal.DateFormatter
import info.metadude.android.eventfahrplan.commons.temporal.Moment

/**
 * Holds the minutes of the day which represent a time segment (hours and minutes).
 * Day, month and year are not considered here.
 *
 * The value is rendered in the time column in the main schedule view.
 */
internal class TimeSegment private constructor(

        minutesOfTheDay: Int

) {

    companion object {

        @JvmStatic
        fun ofMinutesOfTheDay(minutesOfTheDay: Int) = TimeSegment(minutesOfTheDay)

    }

    private val moment: Moment = Moment.now().startOfDay().plusMinutes(minutesOfTheDay.toLong())

    val formattedText: String
        get() = DateFormatter.newInstance().getFormattedTime24Hour(moment)

    fun isMatched(otherMoment: Moment, offset: Int) =
            otherMoment.hour == moment.hour &&
                    otherMoment.minute >= moment.minute &&
                    otherMoment.minute < moment.minute + offset

}

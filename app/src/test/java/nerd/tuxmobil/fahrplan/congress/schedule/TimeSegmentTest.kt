package nerd.tuxmobil.fahrplan.congress.schedule

import info.metadude.android.eventfahrplan.commons.temporal.Moment
import info.metadude.android.eventfahrplan.commons.temporal.Moment.Companion.MILLISECONDS_OF_ONE_MINUTE
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.TimeZone

class TimeSegmentTest {

    @Test
    fun `formattedText returns 01_00 for 0`() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"))
        val segment = TimeSegment.ofMinutesOfTheDay(0)
        assertThat(segment.formattedText).isEqualTo("01:00")
    }

    @Test
    fun `formattedText returns 03_00 for 120`() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"))
        val segment = TimeSegment.ofMinutesOfTheDay(120)
        assertThat(segment.formattedText).isEqualTo("03:00")
    }

    @Test
    fun `formattedText returns 12_00 for 660`() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"))
        val segment = TimeSegment.ofMinutesOfTheDay(660)
        assertThat(segment.formattedText).isEqualTo("12:00")
    }

    @Test
    fun `formattedText returns 00_45 for 1425`() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"))
        val segment = TimeSegment.ofMinutesOfTheDay(1425)
        assertThat(segment.formattedText).isEqualTo("00:45")
    }

    @Test
    fun `isMatched returns true for moment starting 15 minutes later`() {
        val minute = 25
        val segment = TimeSegment.ofMinutesOfTheDay(minute)
        val moment = Moment.ofEpochMilli((minute * MILLISECONDS_OF_ONE_MINUTE).toLong())
        assertThat(segment.isMatched(moment, 15)).isEqualTo(true)
    }

}

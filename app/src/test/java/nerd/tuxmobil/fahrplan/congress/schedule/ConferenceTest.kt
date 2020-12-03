package nerd.tuxmobil.fahrplan.congress.schedule

import com.google.common.truth.Truth.assertThat
import info.metadude.android.eventfahrplan.commons.temporal.Moment
import nerd.tuxmobil.fahrplan.congress.models.Session
import org.junit.Test

class ConferenceTest {

    @Test
    fun `default values`() {
        val conference = Conference()
        assertThat(conference.firstSessionStartsAt).isEqualTo(0)
        assertThat(conference.lastSessionEndsAt).isEqualTo(0)
    }

    @Test
    fun `calculateTimeFrame with frab data spanning multiple days`() {
        val opening = createSession("Opening", duration = 30, dateUtc = 1536332400000L) // 2018-09-07T17:00:00+02:00
        val closing = createSession("Closing", duration = 30, dateUtc = 1536504300000L) // 2018-09-09T16:45:00+02:00
        val (firstSessionStartsAt, lastSessionEndsAt) = createConference(opening, closing)
        assertThat(firstSessionStartsAt).isEqualTo(17 * 60 - 2 * 60) // 17:00h -2h zone offset = 15:00h
        assertThat(lastSessionEndsAt).isEqualTo(1035 - 2 * 60 + Conference.ONE_DAY) // -> 17:15 -2h zone offset + day switch
    }

    @Test
    fun `calculateTimeFrame with frab data spanning a single day`() {
        val opening = createSession("Opening", duration = 30, dateUtc = 1536332400000L) // 2018-09-07T17:00:00+02:00
        val closing = createSession("Closing", duration = 30, dateUtc = 1536336000000L) // 2018-09-07T18:00:00+02:00
        val (firstSessionStartsAt, lastSessionEndsAt) = createConference(opening, closing)
        assertThat(firstSessionStartsAt).isEqualTo(17 * 60 - 2 * 60) // 17:00h -2h zone offset = 15:00h
        assertThat(lastSessionEndsAt).isEqualTo(18 * 60 + 30 - 2 * 60) // -> 18:00 -2h zone offset
    }

    @Test
    fun `calculateTimeFrame with frab data spanning a single day in non-chronological order`() {
        val opening = createSession("Opening", duration = 30, dateUtc = 1536328800000L) // 2018-09-07T16:00:00+02:00
        val middle = createSession("Middle", duration = 20, dateUtc = 1536332400000L) // 2018-09-07T17:00:00+02:00
        val closing = createSession("Closing", duration = 30, dateUtc = 1536336000000L) // 2018-09-07T18:00:00+02:00
        val (firstSessionStartsAt, lastSessionEndsAt) = createConference(opening, closing, middle)
        assertThat(firstSessionStartsAt).isEqualTo(16 * 60 - 2 * 60) // 16:00h -2h zone offset = 14:00h
        assertThat(lastSessionEndsAt).isEqualTo(18 * 60 + 30 - 2 * 60) // -> 18:00 -2h zone offset
    }

    @Test
    fun `calculateTimeFrame with Pentabarf data`() {
        val opening = createSession("Opening", duration = 25, relStartTime = 570) // 09:30
        val closing = createSession("Closing", duration = 10, relStartTime = 1070) // 17:50
        val (firstSessionStartsAt, lastSessionEndsAt) = createConference(opening, closing)
        assertThat(firstSessionStartsAt).isEqualTo(570) // 09:30
        assertThat(lastSessionEndsAt).isEqualTo(1080) // 18:00
    }

    @Test
    fun `calculateTimeFrame with Pentabarf data in non-chronological order`() {
        val opening = createSession("Opening", duration = 25, relStartTime = 570) // 09:30
        val middle = createSession("Middle", duration = 25, relStartTime = 720) // 12:00
        val closing = createSession("Closing", duration = 10, relStartTime = 1070) // 17:50
        val (firstSessionStartsAt, lastSessionEndsAt) = createConference(opening, closing, middle)
        assertThat(firstSessionStartsAt).isEqualTo(570) // 09:30
        assertThat(lastSessionEndsAt).isEqualTo(1080) // 18:00
    }

    private fun createConference(vararg sessions: Session) = Conference().apply {
        calculateTimeFrame(listOf(*sessions)) { dateUtc: Long ->
            Moment.ofEpochMilli(dateUtc).minuteOfDay
        }
    }

    private fun createSession(sessionId: String, duration: Int, dateUtc: Long) = Session(sessionId).apply {
        this.dateUTC = dateUtc
        this.duration = duration
    }

    private fun createSession(sessionId: String, duration: Int, relStartTime: Int) = Session(sessionId).apply {
        this.duration = duration
        this.relStartTime = relStartTime
    }

}

package info.metadude.android.eventfahrplan.commons.temporal

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit

/**
 * An instance represents a moment in time. All operations are UTC based.
 * Timezone based dates are retrieved using [Moment.toZonedDateTime].
 *
 * E.g.
 *
 * > Moment.now().toZonedDateTime(ZoneOffset.of("GMT+1"))
 */
class Moment private constructor(private val time: Instant) {

    val year: Int
        get() = time.atZone(ZoneOffset.UTC).year

    val month: Int
        get() = time.atZone(ZoneOffset.UTC).monthValue

    val monthDay: Int
        get() = time.atZone(ZoneOffset.UTC).dayOfMonth

    val hour: Int
        get() = time.atZone(ZoneOffset.UTC).hour

    val minute: Int
        get() = time.atZone(ZoneOffset.UTC).minute

    val minuteOfDay: Int
        get() = time.atZone(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_DAY)

    /**
     * Returns a copy of this moment, reset to 00:00 hours.
     * Example: 2019-12-31 01:30 => 2019-12-31 00:00
     */
    fun startOfDay(): Moment {
        return Moment(toUtcDateTime()
                .toLocalDate()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC))
    }

    /**
     * Returns a copy of this moment, reset to 23h 59m 59.999s.
     * Example: 2019-12-31 01:30 => 2019-12-31 23:59:59.999
     */
    fun endOfDay(): Moment {
        return Moment(toUtcDateTime()
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .toInstant(ZoneOffset.UTC))
    }

    /**
     * Returns this moment represented as milliseconds.
     */
    fun toMilliseconds() = time.toEpochMilli()

    /**
     * Returns this moment as local date normalized to UTC.
     */
    fun toUtcDateTime(): LocalDateTime = time.atZone(ZoneOffset.UTC).toLocalDateTime()

    /**
     * Returns this moment in given [ZoneOffset].
     */
    fun toZonedDateTime(timeZoneOffset: ZoneOffset): ZonedDateTime = time.atZone(timeZoneOffset)

    /**
     * Returns a moment with the given [hours] subtracted.
     */
    fun minusHours(hours: Long): Moment = Moment(time.minus(hours, ChronoUnit.HOURS))

    /**
     * Returns a moment with the given [minutes] subtracted.
     */
    fun minusMinutes(minutes: Long): Moment = Moment(time.minus(minutes, ChronoUnit.MINUTES))

    /**
     * Returns a moment with the given [seconds] added.
     */
    fun plusSeconds(seconds: Long): Moment = Moment(time.plusSeconds(seconds))

    /**
     * Returns a moment with the given [minutes] added.
     */
    fun plusMinutes(minutes: Long): Moment = Moment(time.plus(minutes, ChronoUnit.MINUTES))

    /**
     * Returns true if this moment is before given [moment].
     */
    fun isBefore(moment: Moment): Boolean = time.toEpochMilli() < moment.toMilliseconds()

    override fun equals(other: Any?): Boolean {
        return time == (other as? Moment)?.time
    }

    override fun hashCode(): Int = time.hashCode()

    override fun toString(): String = time.toString()

    companion object {

        /**
         * 1 minute = 60 seconds
         */
        private const val SECONDS_OF_ONE_MINUTE: Int = 60

        /**
         * Creates a time zone neutral [Moment] instance of current system clock.
         */
        @JvmStatic
        fun now() = Moment(Instant.now())

        /**
         * Returns the amount of minutes between the UTC and the system default time zone.
         */
        @JvmStatic
        fun getSystemOffsetMinutes(): Int {
            val dateTime = LocalDateTime.now()
            val utcDateTime = ZonedDateTime.of(dateTime, ZoneId.of("UTC"))
            val systemDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
            return systemDateTime.offset.totalSeconds / SECONDS_OF_ONE_MINUTE
        }

        /**
         * Creates a time zone neutral [Moment] instance from given [milliseconds].
         *
         * @param milliseconds epoch millis to create instance from
         */
        @JvmStatic
        fun ofEpochMilli(milliseconds: Long) = Moment(Instant.ofEpochMilli(milliseconds))

        /**
         * Creates a time zone neutral [Moment] instance from given [utcDate].
         *
         * @param utcDate must be in ISO-8601 date format, i.e. "yyyy-MM-dd"
         */
        fun parseDate(utcDate: String) = Moment(LocalDate.parse(utcDate).atStartOfDay().toInstant(ZoneOffset.UTC))

        /**
         * Creates a time zone neutral [Moment] instance from this [ZonedDateTime].
         */
        fun ZonedDateTime.toMoment() = Moment(this.withZoneSameInstant(ZoneOffset.UTC).toInstant())
    }
}

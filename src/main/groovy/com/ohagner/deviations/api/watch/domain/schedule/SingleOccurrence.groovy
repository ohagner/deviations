package com.ohagner.deviations.api.watch.domain.schedule

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.ohagner.deviations.config.Constants

import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import static com.ohagner.deviations.config.Constants.ZONE_ID

@Slf4j
@TupleConstructor(force = true)
class SingleOccurrence extends Schedule {

    private final static ObjectMapper mapper = new ObjectMapper()

    @JsonFormat(pattern=Constants.Date.SHORT_DATE_FORMAT, shape=STRING)
    LocalDate dateOfEvent
    @JsonFormat(pattern=Constants.Date.TIME_FORMAT, shape=STRING)
    LocalTime timeOfEvent

    @JsonCreator
    SingleOccurrence(@JsonProperty("dateOfEvent")String dateString, @JsonProperty("timeOfEvent")String timeString) {
        this.dateOfEvent = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        this.timeOfEvent = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
    }

    @Override
    boolean isEventWithinPeriod(LocalDateTime now, long hoursBefore) {
        LocalDateTime eventDateTime = LocalDateTime.of(dateOfEvent, timeOfEvent)
        //boolean isCorrectDay = dateOfEvent.isEqual(now.toLocalDate())
        boolean isWithinTime = eventDateTime.minusHours(hoursBefore).isBefore(now) && eventDateTime.isAfter(now)
        log.debug "IsEventWithinPeriod. Time: $now within $hoursBefore hours before $timeOfEvent on day $dateOfEvent = $isWithinTime"
        return isWithinTime
    }

    @Override
    boolean isTimeToArchive() {
        return LocalDateTime.now(ZONE_ID).isAfter(LocalDateTime.of(dateOfEvent, timeOfEvent))
    }

    String toJson() {
        return mapper.writeValueAsString(this)
    }

    @JsonProperty("timeToArchive")
    void setTimeToArchive(boolean timeToArchive) {}

    @Override
    public String toString() {
        return "SingleOccurrence{" +
                "dateOfEvent=" + dateOfEvent +
                ", timeOfEvent=" + timeOfEvent +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SingleOccurrence that = (SingleOccurrence) o

        if (dateOfEvent != that.dateOfEvent) return false
        if (timeOfEvent != that.timeOfEvent) return false

        return true
    }

    int hashCode() {
        int result
        result = (dateOfEvent != null ? dateOfEvent.hashCode() : 0)
        result = 31 * result + (timeOfEvent != null ? timeOfEvent.hashCode() : 0)
        return result
    }
}


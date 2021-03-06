package com.ohagner.deviations.api.watch.domain.schedule

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.TupleConstructor

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING

@TupleConstructor(force = true)
class WeeklySchedule extends Schedule {

    List<DayOfWeek> weekDays

    @JsonFormat(pattern="HH:mm", shape=STRING)
    LocalTime timeOfEvent

    @JsonCreator
    WeeklySchedule(@JsonProperty("timeOfEvent") String timeOfEventString, @JsonProperty("weekdays")List<DayOfWeek> weekDays) {
        def timeParts = timeOfEventString.split(":").collect { Integer.parseInt(it)}
        this.timeOfEvent = LocalTime.of(timeParts[0], timeParts[1])
        this.weekDays = weekDays
    }

    @Override
    boolean isEventWithinPeriod(LocalDateTime now, long hoursBefore) {
        DayOfWeek weekdayToConsider = now.dayOfWeek
        LocalDateTime eventDateTime = LocalDateTime.of(now.toLocalDate(), timeOfEvent)
        if(timeOfEvent.isBefore(now.toLocalTime())) {
            weekdayToConsider = weekdayToConsider.plus(1)
            eventDateTime = eventDateTime.plusDays(1)
        }
        boolean scheduleHasWeekday = weekDays.contains(weekdayToConsider)

        boolean isWithinTime = eventDateTime.minusHours(hoursBefore).isBefore(now) && eventDateTime.isAfter(now)
        return scheduleHasWeekday && isWithinTime
    }

    @Override
    boolean isTimeToArchive() {
        return false
    }

    @JsonProperty("timeToArchive")
    void setTimeToArchive(boolean timeToArchive) {}

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        WeeklySchedule that = (WeeklySchedule) o

        if (timeOfEvent != that.timeOfEvent) return false
        if (weekDays != that.weekDays) return false

        return true
    }

    int hashCode() {
        int result
        result = (weekDays != null ? weekDays.hashCode() : 0)
        result = 31 * result + (timeOfEvent != null ? timeOfEvent.hashCode() : 0)
        return result
    }
}

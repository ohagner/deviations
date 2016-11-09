package com.ohagner.deviations.domain.user

import com.fasterxml.jackson.annotation.JsonFormat
import com.ohagner.deviations.config.DateConstants
import groovy.transform.TupleConstructor

import java.time.LocalDate

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING

@TupleConstructor(force = true)
class Token {

    String value

    @JsonFormat(pattern=DateConstants.SHORT_DATE_FORMAT, shape=STRING)
    LocalDate expirationDate

//    @JsonCreator
//    Token(@JsonProperty("value")String value, @JsonProperty("expirationDate")String expirationDate) {
//        this.value = value
//        this.expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE)
//    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Token token = (Token) o

        if (expirationDate != token.expirationDate) return false
        if (value != token.value) return false

        return true
    }

    int hashCode() {
        int result
        result = (value != null ? value.hashCode() : 0)
        result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0)
        return result
    }
}

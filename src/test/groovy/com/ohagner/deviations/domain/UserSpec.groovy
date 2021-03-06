package com.ohagner.deviations.domain

import com.ohagner.deviations.api.user.domain.Role
import com.ohagner.deviations.api.user.domain.Credentials
import com.ohagner.deviations.api.user.domain.Token
import com.ohagner.deviations.api.user.domain.User
import com.ohagner.deviations.api.user.domain.Webhook
import spock.lang.Specification
import java.time.LocalDate

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static org.hamcrest.MatcherAssert.assertThat

class UserSpec extends Specification {

    void 'create json from User object'() {
        given:
            String expected = new File("src/test/resources/users/user.json").text
            User user = createUser()
        expect:
            assertThat(user.toJson(), jsonEquals(expected))
    }

    void 'create User object from json'() {
        given:
            String userAsJson = new File("src/test/resources/users/user.json").text
            User expected = createUser()
        expect:
            assert User.fromJson(userAsJson).equals(expected)
    }

    private User createUser() {
        Credentials credentials = Credentials.builder()
            .role(Role.USER)
            .username("username")
            .passwordHash("passwordHash")
            .passwordSalt("passwordSalt")
            .apiToken(new Token(value: "value", expirationDate: LocalDate.of(2016, 10, 10))).build()
        return User.builder()
                .credentials(credentials)
                .firstName("firstName")
                .lastName("lastName")
                .emailAddress("emailAddress")
                .webhook(Webhook.newInstance("http://webhook"))
                .slackWebhook(Webhook.newInstance("http://slackwebhook"))
                .build()
    }

}

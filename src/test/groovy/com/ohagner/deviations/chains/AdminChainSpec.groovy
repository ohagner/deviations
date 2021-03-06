package com.ohagner.deviations.chains

import com.ohagner.deviations.api.user.router.AdminChain
import com.ohagner.deviations.api.notification.endpoint.SendNotificationHandler
import ratpack.groovy.test.handling.GroovyRequestFixture
import ratpack.handling.Handlers
import spock.lang.Specification

class AdminChainSpec extends Specification {

    void 'should call SendNotificationHandler'() {
        when:
            def result = GroovyRequestFixture
                    .requestFixture()
                    .handle(new AdminChain()) {
                uri "users/username/notification"
                method "POST"
                registry.add(SendNotificationHandler, Handlers.post())
            }
        then:
            result.isCalledNext()
    }


}

package com.ohagner.deviations.modules

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import com.ohagner.deviations.api.user.repository.UserRepository
import com.ohagner.deviations.api.user.service.security.AuthenticationService
import com.ohagner.deviations.api.user.service.security.DefaultAuthenticationService
import com.ohagner.deviations.web.user.service.DefaultUserService
import com.ohagner.deviations.web.user.service.UserService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import ratpack.http.client.HttpClient
import ratpack.server.PublicAddress

@Slf4j
class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @CompileStatic
    @Singleton
    @Inject
    UserService provideUserService(PublicAddress publicAddress, HttpClient httpClient) {
        return new DefaultUserService(publicAddress, httpClient)
    }

    @Provides
    @CompileStatic
    @Singleton
    @Inject
    AuthenticationService provideAuthenticationService(UserRepository userRepository) {
        return new DefaultAuthenticationService(userRepository)
    }

}

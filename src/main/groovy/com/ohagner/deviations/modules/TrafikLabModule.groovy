package com.ohagner.deviations.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.ohagner.deviations.repository.CachedDeviationRepository
import com.ohagner.deviations.repository.DeviationRepository
import com.ohagner.deviations.repository.HttpDeviationRepository
import com.ohagner.deviations.config.TrafikLabConfig
import groovy.transform.CompileStatic
import wslite.rest.RESTClient

import java.time.Duration

class TrafikLabModule extends AbstractModule {

    TrafikLabConfig config

    @Override
    protected void configure() {
        config = TrafikLabConfig.getInstance()
    }

    @Provides
    @CompileStatic
    DeviationRepository createDeviationRepo() {
        RESTClient client = new RESTClient(config.baseUrl+config.deviationsResourcePath)
        return new CachedDeviationRepository(new HttpDeviationRepository(client, config.apiKey), Duration.ofMinutes(30))
    }

}

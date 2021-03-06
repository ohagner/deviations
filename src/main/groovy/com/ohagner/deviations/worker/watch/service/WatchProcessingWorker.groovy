package com.ohagner.deviations.worker.watch.service

import com.google.inject.Guice
import com.google.inject.Injector
import com.ohagner.deviations.api.deviation.service.DeviationMatcher
import com.ohagner.deviations.config.AppConfig
import com.ohagner.deviations.config.Constants
import com.ohagner.deviations.api.watch.domain.Watch
import com.ohagner.deviations.api.deviation.module.DeviationsModule
import com.ohagner.deviations.modules.MessagingModule
import com.ohagner.deviations.api.deviation.repository.DeviationRepository
import com.ohagner.deviations.worker.api.service.DefaultDeviationsApiClient
import com.ohagner.deviations.worker.api.service.DeviationsApiClient
import com.ohagner.deviations.worker.watch.service.stage.DeviationMatchingStage
import com.ohagner.deviations.worker.watch.service.stage.LoggingStage
import com.ohagner.deviations.worker.watch.service.stage.NotificationStage
import com.ohagner.deviations.worker.watch.service.stage.TimeToCheckStage
import com.ohagner.deviations.worker.watch.service.stage.UpdatingStage
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

import groovy.transform.builder.Builder
import groovy.util.logging.Slf4j
import wslite.rest.RESTClient


@Slf4j
@Builder
class WatchProcessingWorker  {

    DeviationsApiClient apiClient
    DeviationRepository deviationRepository


    def static main(args) {
        String baseUrl = AppConfig.envOrProperty("DEVIATIONS_URL")
        if(!baseUrl) {
            log.error "No URL found for api. Exiting..."
            System.exit(-1)
        }

        Injector deviationsInjector = Guice.createInjector(new DeviationsModule())
        DeviationRepository deviationRepository = deviationsInjector.getInstance(DeviationRepository)

        Injector messagingInjector = Guice.createInjector(new MessagingModule())
        Channel channel = messagingInjector.getInstance(Channel)

        WatchProcessingWorker worker = new WatchProcessingWorker(apiClient: new DefaultDeviationsApiClient(client: new RESTClient(baseUrl)), deviationRepository: deviationRepository)
        try {
            worker.handleIncomingWork(channel)
        } catch(Exception e) {
            log.error("Watch processing worker process failed", e)
        }

    }

    private void handleIncomingWork(Channel channel) {

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                String message = new String(body, "UTF-8");
                Watch watch = Watch.fromJson(message)

                DeviationMatcher deviationMatcher = new DeviationMatcher(deviationRepository.retrieveAll())
                WatchProcessingChain chain = new WatchProcessingChain()
                chain.appendStage(new TimeToCheckStage())
                chain.appendStage(new DeviationMatchingStage(deviationMatcher: deviationMatcher))
                chain.appendStage(new NotificationStage(deviationsApiClient: apiClient))
                chain.appendStage(new UpdatingStage(deviationsApiClient: apiClient))
                chain.appendStage(new LoggingStage())
                chain.process(watch)
            }

        }
        channel.basicConsume(Constants.WATCHES_TO_PROCESS_QUEUE_NAME, true, consumer)
        log.info "Started watch processing queue consumer"
    }

}

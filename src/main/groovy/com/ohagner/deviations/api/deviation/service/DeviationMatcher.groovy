package com.ohagner.deviations.api.deviation.service

import com.ohagner.deviations.api.deviation.domain.Deviation
import com.ohagner.deviations.api.transport.domain.Transport
import com.ohagner.deviations.api.watch.domain.Watch
import groovy.util.logging.Slf4j

@Slf4j
class DeviationMatcher {
    final Map<Transport, List<Deviation>> transportDeviationMap

    public DeviationMatcher(List<Deviation> deviationList) {

        log.debug "Initializing DeviationMatcher with ${deviationList?.size()} deviations"
        transportDeviationMap = new HashMap<>()

        deviationList
            .each { deviation ->
                deviation.lineNumbers.each { lineNumber ->
                    def transport = new Transport(transportMode: deviation.transportMode, line: lineNumber)
                    log.debug "Adding deviation to matcher with linenumber $lineNumber and transportMode ${deviation.transportMode}"
                    transportDeviationMap.get(transport, []).add(deviation)
                }
            }
        transportDeviationMap.each { key, value -> log.debug "Adding transport/deviation key: ${key.toString()}, value: $value"}
    }

    Set<Deviation> findMatching(Watch watch) {
        log.debug "Matching watch: $watch"
        Set<Deviation> matchingDeviations = []
        watch.transports.each {
            matchingDeviations.addAll(transportDeviationMap.get(it, []))
        }
        log.debug "Matching watch: ${watch.name}. Found ${matchingDeviations.size()} match(es)."
        return matchingDeviations.findAll { watch.processedDeviationIds.contains(it.id) == false }
    }


}

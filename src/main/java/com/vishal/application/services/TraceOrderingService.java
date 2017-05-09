package com.vishal.application.services;

import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by vishal.joshi on 5/8/17.
 */
@Component
public class TraceOrderingService {

    private LogLineInfoOrganisationService logLineInfoOrganisationService;

    @Autowired
    public TraceOrderingService(LogLineInfoOrganisationService logLineInfoOrganisationService) {
        this.logLineInfoOrganisationService = logLineInfoOrganisationService;
    }

    public List<Trace> orderByEarliestFinishingSpan(List<Trace> traceList, Map<String, List<LogLineInfo>> traceIdVsLogLineInfo) {
        List<String> orderedListOfTraceIds = logLineInfoOrganisationService.orderTraceIdsByEarliestFinishingSpan(traceIdVsLogLineInfo);
        List<Trace> listOfOrderedTraceObjects = new ArrayList<>();
        for (Iterator<String> orderedTraceIdsIterator = orderedListOfTraceIds.iterator(); orderedTraceIdsIterator.hasNext(); ) {
            String traceId = orderedTraceIdsIterator.next();
            for (Iterator<Trace> traceIdIterator = traceList.iterator(); traceIdIterator.hasNext(); ) {
                Trace trace = traceIdIterator.next();
                if (trace.getId().equals(traceId)) {
                    // Remove the current element from the iterator and the list.
                    traceIdIterator.remove();
                    listOfOrderedTraceObjects.add(trace);
                    break;
                }
            }
            orderedTraceIdsIterator.remove();
        }
        return listOfOrderedTraceObjects;
    }
}

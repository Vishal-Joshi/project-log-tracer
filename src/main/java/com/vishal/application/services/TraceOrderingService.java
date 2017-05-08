package com.vishal.application.services;

import com.vishal.application.entity.Trace;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Created by vishal.joshi on 5/8/17.
 */
@Component
public class TraceOrderingService {

    public List<Trace> orderByFirstCallSpanStartDate(List<Trace> traceList) {
        traceList.sort(Comparator.comparing(trace -> trace.getRoot().getStart()));
        return traceList;
    }
}

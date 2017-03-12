package com.skynet.pipeline;

import java.util.Map;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

public class Pipeline {

    private String id;

    private Processor headProcessor;

    private Isolation isolation;

    private Propagation propagation;

    private Map<String, Processor> processors;

    public Processor getHeadProcessor() {
        return this.headProcessor;
    }

    public String getId() {
        return this.id;
    }

    public Isolation getIsolation() {
        return this.isolation;
    }

    public Map<String, Processor> getProcessors() {
        return this.processors;
    }

    public Propagation getPropagation() {
        return this.propagation;
    }

    public void setHeadProcessor(final Processor headProcessor) {
        this.headProcessor = headProcessor;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setIsolation(final Isolation isolation) {
        this.isolation = isolation;
    }

    public void setProcessors(final Map<String, Processor> processors) {
        this.processors = processors;
    }

    public void setPropagation(final Propagation propagation) {
        this.propagation = propagation;
    }

}

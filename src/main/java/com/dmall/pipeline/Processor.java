package com.dmall.pipeline;

import java.util.Map;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

public class Processor {

    private String id;

    private AbstractPipelineProcessor beanRef;

    private Isolation isolation;

    private Propagation propagation;

    private Map<Integer, Processor> results;

    public AbstractPipelineProcessor getBeanRef() {
        this.beanRef.setProcessorDef(this);
        return this.beanRef;
    }

    public String getId() {
        return this.id;
    }

    public Isolation getIsolation() {
        return this.isolation;
    }

    public Propagation getPropagation() {
        return this.propagation;
    }

    public Map<Integer, Processor> getResults() {
        return this.results;
    }

    public void setBeanRef(final AbstractPipelineProcessor beanRef) {
        this.beanRef = beanRef;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setIsolation(final Isolation isolation) {
        this.isolation = isolation;
    }

    public void setPropagation(final Propagation propagation) {
        this.propagation = propagation;
    }

    public void setResults(final Map<Integer, Processor> results) {
        this.results = results;
    }

}

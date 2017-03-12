package com.dmall.pipeline;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

public abstract class AbstractPipelineProcessor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractPipelineProcessor.class);

    private Processor processorDef;

    int executeProcessor(final Map<String, Object> params)
            throws PipelineException {
        final PipelineEngine pipelineEngine = (PipelineEngine) params
                .get(PipelineEngine.PIPELINE_ENGINE);
        final PlatformTransactionManager transactionManager = pipelineEngine
                .getTransactionManager();
        final TransactionTemplate transactionTemplate = new TransactionTemplate(
                transactionManager);
        if (this.processorDef.getIsolation() != null) {
            transactionTemplate.setIsolationLevel(this.processorDef
                    .getIsolation().value());
        }
        if (this.processorDef.getPropagation() != null) {
            transactionTemplate.setPropagationBehavior(this.processorDef
                    .getPropagation().value());
        }
        transactionTemplate.setName("Processor(" + this.processorDef.getId()
                + ")");
        final Object result = transactionTemplate
                .execute(new TransactionCallback<Object>() {
                    public Object doInTransaction(final TransactionStatus status) {
                        try {
                            final int result = AbstractPipelineProcessor.this
                                    .process(params);
                            if (result == PipelineEngine.STOP_AND_ROLLBACK) {
                                status.setRollbackOnly();
                            }
                            return result;
                        } catch (final PipelineException e) {
                            status.setRollbackOnly();
                            return e;
                        }
                    }
                });
        if (result instanceof PipelineException) {
            throw (PipelineException) result;
        }
        final int resultValue = ((Integer) result).intValue();
        if ((resultValue == PipelineEngine.STOP_AND_ROLLBACK)
                || (resultValue == PipelineEngine.STOP_AND_COMMIT)) {
            return resultValue;
        }
        final Map<Integer, Processor> nextProcessorMap = this.processorDef
                .getResults();
        if (CollectionUtils.isEmpty(nextProcessorMap)) {
            return resultValue;
        }
        final Processor nextProcessorDef = nextProcessorMap.get(resultValue);
        if (nextProcessorDef == null) {
            return resultValue;
        }
        return nextProcessorDef.getBeanRef().executeProcessor(params);
    }

    void setProcessorDef(final Processor processorDef) {
        this.processorDef = processorDef;
    }

    protected abstract int process(Map<String, Object> params)
            throws PipelineException;

}
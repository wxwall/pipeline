package com.skynet.pipeline;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public abstract class AbstractPipelineProcessor {

    private Processor processorDef;

    String executeProcessor(final Map<String, Object> params) throws PipelineException {
        final PipelineEngine pipelineEngine = (PipelineEngine) params.get(PipelineEngine.PIPELINE_ENGINE);
        final PlatformTransactionManager transactionManager = pipelineEngine.getTransactionManager();
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        if (this.processorDef.getIsolation() != null) {
            transactionTemplate.setIsolationLevel(this.processorDef.getIsolation().value());
        }
        if (this.processorDef.getPropagation() != null) {
            transactionTemplate.setPropagationBehavior(this.processorDef.getPropagation().value());
        }
        transactionTemplate.setName("Processor(" + this.processorDef.getId() + ")");
        final Object result = transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(final TransactionStatus status) {
                try {
                    final String result = AbstractPipelineProcessor.this.process(params);
                    if (PipelineEngine.STOP_AND_ROLLBACK.equalsIgnoreCase(result)) {
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
        final String resultValue = (String) result;
        if ((PipelineEngine.STOP_AND_ROLLBACK.equalsIgnoreCase(resultValue)) || PipelineEngine.STOP_AND_COMMIT.equalsIgnoreCase(resultValue)) {
            return resultValue;
        }
        final Map<String, Processor> nextProcessorMap = this.processorDef.getResults();
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

    protected abstract String process(Map<String, Object> params) throws PipelineException;

}
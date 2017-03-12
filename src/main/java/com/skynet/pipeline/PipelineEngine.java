package com.skynet.pipeline;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class PipelineEngine implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineEngine.class);

    protected Map<String, Pipeline> pipelineContainer;

    private PlatformTransactionManager transactionManager;

    private ApplicationContext applicationContext;

    public static final String STOP_AND_COMMIT = "Stop_And_Commit";

    public static final String STOP_AND_ROLLBACK = "Stop_And_Rollback";

    public static final String PIPELINE_ENGINE = "PipelineEngine";

    public void executePipeline(final String pipelineId, final Map<String, Object> params) throws PipelineException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Begin pipeline {}", pipelineId);
        }
        if (this.pipelineContainer == null) {
            throw new PipelineException(MessageFormat.format("Pipeline(\"{0}\") does not exist.", pipelineId));
        }
        final Pipeline pipeline = this.pipelineContainer.get(pipelineId);
        if (pipeline == null) {
            throw new PipelineException(MessageFormat.format("Pipeline(\"{0}\") does not exist.", pipelineId));
        }
        final AbstractPipelineProcessor headProcessor = pipeline.getHeadProcessor().getBeanRef();
        final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        if (pipeline.getIsolation() != null) {
            transactionTemplate.setIsolationLevel(pipeline.getIsolation().value());
        }
        if (pipeline.getPropagation() != null) {
            transactionTemplate.setPropagationBehavior(pipeline.getPropagation().value());
        }
        transactionTemplate.setName("Pipeline(" + pipelineId + ")");
        final Object result = transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(final TransactionStatus status) {
                Map<String, Object> paramMap = params;
                if (paramMap == null) {
                    paramMap = new HashMap<String, Object>();
                }
                paramMap.put(PIPELINE_ENGINE, PipelineEngine.this);
                try {
                    final String result = headProcessor.executeProcessor(paramMap);
                    if (PipelineEngine.STOP_AND_ROLLBACK.equalsIgnoreCase(result)) {
                        status.setRollbackOnly();
                    }
                    return result;
                } catch (final PipelineException e) {
                    status.setRollbackOnly();
                    return e;
                } catch (final Exception e) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    }
                    status.setRollbackOnly();
                    return new PipelineException(e);
                }
            }
        });
        if (result instanceof PipelineException) {
            throw (PipelineException) result;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End pipeline {}", pipelineId);
        }
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void init() {
        this.pipelineContainer = this.applicationContext.getBeansOfType(Pipeline.class);
    }

    public void setApplicationContext(
            final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setTransactionManager(
            final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

}

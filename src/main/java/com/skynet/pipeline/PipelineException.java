/**
 *
 */
package com.skynet.pipeline;

/**
 * @author GaoPeng
 *
 */
public class PipelineException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -9112421835385732878L;

    public PipelineException() {
        super();
    }

    public PipelineException(final String message) {
        super(message);
    }

    public PipelineException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PipelineException(final Throwable cause) {
        super(cause);
    }

}

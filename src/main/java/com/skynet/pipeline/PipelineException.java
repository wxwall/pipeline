/**
 *
 */
package com.skynet.pipeline;

/**
 * @author GaoPeng
 */
public class PipelineException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -9112421835385732878L;

    private Exception targetException;

    public PipelineException(final String message) {
        super(message);
    }

    public PipelineException(final String message, final Exception cause) {
        super(message, cause);
        targetException = cause;
    }

    public PipelineException(final Exception cause) {
        super(cause);
        targetException = cause;
    }

    public Exception getTargetException() {
        return targetException;
    }
}

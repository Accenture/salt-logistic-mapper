package de.salt.sce.mapper.exception;

/**
 * Exception for failed parsed message
 *
 * @author WRH
 * @since 3.0.1
 */
public class ParserFailedException extends Exception {

    private static final long serialVersionUID = -8494846623225127072L;

    public ParserFailedException(String errorMessage) {
        super(errorMessage);
    }

}

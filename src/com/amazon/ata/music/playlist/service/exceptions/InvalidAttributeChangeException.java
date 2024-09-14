package com.amazon.ata.music.playlist.service.exceptions;


public class InvalidAttributeChangeException extends AttributeExceptions {

    private static final long serialVersionUID = 4556832065527511085L;

    public InvalidAttributeChangeException() {
        super();
    }

    public InvalidAttributeChangeException(String message) {
        super(message);
    }

    public InvalidAttributeChangeException(Throwable cause) {
        super(cause);
    }

    public InvalidAttributeChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}


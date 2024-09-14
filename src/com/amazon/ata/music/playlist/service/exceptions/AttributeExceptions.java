package com.amazon.ata.music.playlist.service.exceptions;

public class AttributeExceptions extends RuntimeException {

    private static final long serialVersionUID = 4556832065527519085L;

    public AttributeExceptions() {
        super();
    }

    public AttributeExceptions(String message) {
        super(message);
    }

    public AttributeExceptions(Throwable cause) {
        super(cause);
    }

    public AttributeExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}


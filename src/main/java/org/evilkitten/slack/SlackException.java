package org.evilkitten.slack;

public class SlackException extends RuntimeException {
    public SlackException() {
    }

    public SlackException(String message) {
        super(message);
    }

    public SlackException(String message, Throwable cause) {
        super(message, cause);
    }

    public SlackException(Throwable cause) {
        super(cause);
    }

    public SlackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

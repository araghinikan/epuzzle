package com.nikan.epuzzle.exception;

public class CustomException extends RuntimeException {
    private final MessageCode messageCode;

    public CustomException(MessageCode messageCode) {
        super(messageCode.name());
        this.messageCode = messageCode;
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }
}
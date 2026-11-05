package com.javaforge.notificationservice.exception;

public class NotificationQueueFullException extends RuntimeException {

    public NotificationQueueFullException(Throwable cause) {
        super("Notification queue is full; try again later", cause);
    }
}

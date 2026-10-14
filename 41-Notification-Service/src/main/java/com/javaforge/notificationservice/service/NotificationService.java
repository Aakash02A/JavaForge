package com.javaforge.notificationservice.service;

import com.javaforge.notificationservice.dto.NotificationRequest;
import com.javaforge.notificationservice.dto.NotificationResponse;
import com.javaforge.notificationservice.exception.NotificationNotFoundException;
import com.javaforge.notificationservice.exception.NotificationQueueFullException;
import com.javaforge.notificationservice.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final TaskExecutor taskExecutor;
    private final NotificationSender notificationSender;
    private final ConcurrentMap<UUID, NotificationResponse> notifications = new ConcurrentHashMap<>();

    public NotificationService(TaskExecutor notificationTaskExecutor, NotificationSender notificationSender) {
        this.taskExecutor = notificationTaskExecutor;
        this.notificationSender = notificationSender;
    }

    public NotificationResponse submit(NotificationRequest request) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        NotificationResponse queued = new NotificationResponse(
                id,
                request.channel(),
                NotificationStatus.QUEUED,
                now,
                now,
                "/api/notifications/" + id,
                null
        );
        notifications.put(id, queued);

        try {
            taskExecutor.execute(() -> process(id, request));
        } catch (TaskRejectedException exception) {
            notifications.remove(id, queued);
            throw new NotificationQueueFullException(exception);
        }

        return queued;
    }

    public NotificationResponse get(UUID id) {
        NotificationResponse notification = notifications.get(id);
        if (notification == null) {
            throw new NotificationNotFoundException(id);
        }
        return notification;
    }

    private void process(UUID id, NotificationRequest request) {
        updateStatus(id, NotificationStatus.PROCESSING, null);
        try {
            notificationSender.send(id, request);
            updateStatus(id, NotificationStatus.COMPLETED, null);
        } catch (RuntimeException exception) {
            logger.error("Notification {} could not be processed", id, exception);
            updateStatus(id, NotificationStatus.FAILED, "Notification could not be processed");
        }
    }

    private void updateStatus(UUID id, NotificationStatus status, String failureReason) {
        notifications.computeIfPresent(id, (notificationId, current) -> new NotificationResponse(
                current.id(),
                current.channel(),
                status,
                current.submittedAt(),
                Instant.now(),
                current.statusUrl(),
                failureReason
        ));
    }
}
